(ns status-im.wallet.background-check
  (:require
   [clojure.string :as clojure.string]
   [re-frame.core :as re-frame]
   [status-im.async-storage.core :as async]
   [status-im.ethereum.json-rpc :as json-rpc]
   [status-im.utils.config :as config]
   [status-im.utils.fx :as fx]
   [status-im.utils.money :as money]
   [status-im.utils.platform :as platform]
   [status-im.utils.types :as types]
   [status-im.notifications.local :as local]
   ["react-native-background-fetch" :default background-fetch]
   [taoensso.timbre :as log]))

(defn finish-task [id]
  (.finish ^js background-fetch id))

(re-frame/reg-fx ::finish-task finish-task)

(fx/defn finish [{:keys [db]}]
  {:events [::finish]}
  (let [task-id (get db :wallet/background-fetch-task-id)]
    {:db           (dissoc db :wallet/background-fetch-task-id)
     :local/local-pushes-ios [{:title   "FINISH"
                               :message task-id}]
     ::finish-task task-id}))

(fx/defn configure
  {:events [::configure]}
  [cofx]
  (when platform/ios?
    {:local/local-pushes-ios [{:title   "CONFIGURE"
                               :message "nothing here"}]
     ::json-rpc/call
     [{:method     "wallet_getCachedBalances"
       :params     [(mapv :address (get-in cofx [:db :multiaccount/accounts]))]
       :on-success #(re-frame/dispatch [::retrieved-balances %])
       :on-error   #(re-frame/dispatch [::clean-async-storage])}]}))

(fx/defn retrieved-balances
  {:events [::retrieved-balances]}
  [{:keys [db]} balances]
  (log/debug "Cached balances retrieved" balances)
  {:local/local-pushes-ios [{:title   "STORE BALANCES AND URL"
                             :message (str "balances: " (count balances))}]
   ::async/set!
   {:rpc-url         (config/get-rpc-url db)
    :cached-balances balances}})

(fx/defn clean-async-storage
  {:events [::clean-async-storage]}
  [cofx]
  (fx/merge
   cofx
   {::async/set!
    {:rpc-url         nil
     :cached-balances nil}}
   (finish)))

(fx/defn perform-check
  {:events [::perform-check]}
  [{:keys [db]} task-id]
  (when platform/ios?
    {:db                     (assoc db :wallet/background-fetch-task-id task-id)
     :local/local-pushes-ios [{:title   "PERFORM CHECK"
                               :message task-id}]
     ::async/get             {:keys [:rpc-url :cached-balances]
                              :cb   #(re-frame/dispatch [::retrieve-latest-balances %])}}))

(defn- prepare-batch-request [method addresses]
  (str
   "["
   (clojure.string/join
    ","
    (mapcat
     (fn [address]
       (map
        types/clj->json
        [{:jsonrpc "2.0"
          :id      address
          :method  method
          :params  [address "latest"]}]))
     addresses))
   "]"))

(defn parse-rpc-response [raw-response]
  (-> raw-response
      :response-body
      types/json->clj))

(fx/defn retrieve-latest-balances
  {:events [::retrieve-latest-balances]}
  [cofx {:keys [rpc-url cached-balances] :as configs}]
  (log/debug "Configuration retrieved" configs)
  (let [addresses (mapv :address cached-balances)]
    {:local/local-pushes-ios [{:title   "RETRIEVED CACHED BALANCES"
                               :message (str rpc-url " " (count cached-balances))}]
     :http-post
     {:url      rpc-url
      :data     (prepare-batch-request "eth_getBalance" addresses)
      :on-success
      (fn [result]
        (re-frame/dispatch [::retrieve-latest-nonces
                            addresses
                            configs
                            (parse-rpc-response result)]))
      :on-error #(re-frame/dispatch [::finish %])}}))

(fx/defn retrieve-latest-nonces
  {:events [::retrieve-latest-nonces]}
  [cofx addresses {:keys [rpc-url] :as configs} balances]
  {:local/local-pushes-ios [{:title   "RETRIEVED BALANCES"
                             :message (str "latest balances: " (count balances))}]
   :http-post
   {:url        rpc-url
    :data       (prepare-batch-request "eth_getTransactionCount" addresses)
    :on-success (fn [result]
                  (re-frame/dispatch [::check-results
                                      addresses
                                      configs
                                      balances
                                      (parse-rpc-response result)]))
    :on-error   #(re-frame/dispatch [::finish %])}})

(defn prepare-results
  ([k data] (prepare-results {} k data))
  ([acc k data]
   (reduce
    (fn [acc {:keys [id result]}]
      (assoc-in acc [id k] (money/bignumber result)))
    acc
    data)))

(fx/defn update-cache
  [_ cached-balances addresses latest]
  (when (seq addresses)
    (let [balances (mapv (fn [{:keys [address] :as cache}]
                           (assoc cache
                                  :balance (money/to-string
                                            (get-in latest [address :balance]))
                                  :nonce (money/to-string
                                          (get-in latest [address :nonce]))))
                         cached-balances)]
      {::async/set!
       {:cached-balances balances}})))

(fx/defn notify
  [cofx addresses]
  {:local/local-pushes-ios
   (mapv (fn [address]
           {:title   "TRANSACTION DETECTED"
            :message address})
         addresses)})

(fx/defn check-results
  {:events [::check-results]}
  [cofx addresses {:keys [cached-balances]} balances nonces]
  (let [latest (->
                (prepare-results :balance balances)
                (prepare-results :nonce nonces))
        addresses-with-changes
        (keep
         (fn [{:keys [address balance nonce]}]
           (when (or (and
                      (get-in latest [address :balance])
                      (not (money/equals
                            (money/bignumber balance)
                            (get-in latest [address :balance]))))
                     (and
                      (get-in latest [address :nonce])
                      (not (money/equals
                            (money/bignumber nonce)
                            (get-in latest [address :nonce])))))
             address))
         cached-balances)]
    (fx/merge
     cofx
     {:local/local-pushes-ios [{:title "TASK FINISHED"
                                :message (str addresses-with-changes)}]}
     (update-cache cached-balances addresses-with-changes latest)
     (notify addresses-with-changes)
     (finish))))

(defn on-event [task-id]
  (re-frame.core/dispatch [::perform-check task-id]))

(defn on-timeout [task-id]
  (local/local-push-ios
   {:title "ON TIMEOUT"
    :message (str task-id)})
  (finish-task task-id))

(defn start-background-fetch []
  (when platform/ios?
    (.then
     (.configure ^js background-fetch
                 #js {:minimumFetchInterval 20}
                 on-event
                 on-timeout)
     (fn [status]
       (local/local-push-ios
        {:title   "START FETCHING"
         :message (str status)})))))
