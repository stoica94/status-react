(ns status-im.chat.models.pin-message
  (:require [status-im.chat.models.message-list :as message-list]
            [status-im.constants :as constants]
            [status-im.data-store.messages :as data-store.messages]
            [status-im.utils.fx :as fx]
            [taoensso.timbre :as log]
            [re-frame.core :as re-frame]
            [status-im.chat.models.loading :as loading]))

(fx/defn handle-failed-loading-pin-messages
  {:events [::failed-loading-pin-messages]}
  [{:keys [db]} current-chat-id _ err]
  (log/error "failed loading pin messages" current-chat-id err)
  (when current-chat-id
    {:db (assoc-in db [:pagination-info current-chat-id :loading-pin-messages?] false)}))

(fx/defn pin-messages-loaded
  {:events [::pin-messages-loaded]}
  [{db :db} chat-id session-id {:keys [cursor messages]}]
  (when-not (and (get-in db [:pagination-info chat-id :pin-messages-initialized?])
                 (not= session-id
                       (get-in db [:pagination-info chat-id :pin-messages-initialized?])))
    (let [pin-messages (map #(assoc-in % [:pinned?] true)
                            messages)
          already-loaded-pin-messages (get-in db [:pin-messages chat-id] {})
          {:keys [all-messages new-messages]} (reduce (fn [{:keys [all-messages] :as acc}
                                                           {:keys [message-id alias from]
                                                            :as   message}]
                                                        (let [message-pin (merge message
                                                                                 {:pinned-by from})]
                                                          (cond-> acc
                                                            (nil? (get all-messages message-id))
                                                            (update :new-messages conj message-pin)

                                                            :always
                                                            (update :all-messages assoc message-id message-pin))))
                                                      {:all-messages already-loaded-pin-messages
                                                       :new-messages []}
                                                      pin-messages)
          messages-id-list (map :message-id pin-messages)
          current-clock-value (get-in db [:pagination-info chat-id :pin-cursor-clock-value])
          clock-value (when cursor (loading/cursor->clock-value cursor))]
      {:db (-> db
               (update-in [:pagination-info chat-id :pin-cursor-clock-value]
                          #(if (and (seq cursor) (or (not %) (< clock-value %)))
                             clock-value
                             %))
               (update-in [:pagination-info chat-id :pin-cursor]
                          #(if (or (empty? cursor) (not current-clock-value) (< clock-value current-clock-value))
                             cursor
                             %))
               (assoc-in [:pagination-info chat-id :loading-pin-messages?] false)
               (assoc-in [:pin-messages chat-id] all-messages)
               (update-in [:pin-message-lists chat-id] message-list/add-many new-messages)
               (assoc-in [:pagination-info chat-id :all-pin-loaded?]
                         (empty? cursor)))})))

(fx/defn receive-signal
  [{:keys [db] :as cofx} pin-messages]
  (let [{:keys [chat-id]} (first pin-messages)]
    (when (= chat-id (db :current-chat-id))
      (print "dasj")
      (let [{:keys [chat-id]} (first pin-messages)
            already-loaded-pin-messages (get-in db [:pin-messages chat-id] {})
            already-loaded-messages (get-in db [:messages chat-id] {})
            {:keys [all-messages new-messages]} (reduce (fn [{:keys [all-messages] :as acc}
                                                             {:keys [message_id pinned from]
                                                              :as   message}]
                                                          (let [current-message (get already-loaded-messages message_id)
                                                                current-message-pin (merge current-message
                                                                                           {:pinned    pinned
                                                                                            :pinned-by from})]
                                                            (cond-> acc
                                                              (and pinned (nil? current-message))
                                                              (update :new-messages conj current-message-pin)

                                                              (nil? pinned)
                                                              (update :all-messages dissoc message_id)

                                                              (and (some? pinned) (some? current-message))
                                                              (update :all-messages assoc message_id current-message-pin))))
                                                        {:all-messages already-loaded-pin-messages
                                                         :new-messages []}
                                                        pin-messages)]
        {:db (-> db
                 (assoc-in [:pin-messages chat-id] all-messages)
                 (assoc-in [:pin-message-lists chat-id]
                           (message-list/add-many nil (vals all-messages))))}))))

(fx/defn load-more-pin-messages
  {:events [:load-more-pin-messages]}
  [{:keys [db]} chat-id first-request]
  (let [session-id (get-in db [:pagination-info chat-id :pin-messages-initialized?])
        not-all-loaded? (not (get-in db [:pagination-info chat-id :all-loaded?]))
        not-loading-pin-messages? (not (get-in db [:pagination-info chat-id :loading-pin-messages?]))
        cursor (get-in db [:pagination-info chat-id :pin-cursor])]
    (when (and session-id
               not-loading-pin-messages?
               (or cursor first-request))
      (fx/merge
       {:db (assoc-in db [:pagination-info chat-id :loading-pin-messages?] true)}
       (data-store.messages/pinned-message-by-chat-id-rpc
        chat-id
        cursor
        constants/default-number-of-pin-messages
        #(re-frame/dispatch [::pin-messages-loaded chat-id session-id %])
        #(re-frame/dispatch [::failed-loading-pin-messages chat-id session-id %]))))))

(fx/defn send-pin-message
  "Pin message, rebuild pinned messages list"
  {:events [::send-pin-message]}
  [{:keys [db] :as cofx} {:keys [chat-id message-id pinned?] :as pin-message}]
  (let [current-public-key (get-in db [:multiaccount :public-key])
        message (merge pin-message {:pinned-by current-public-key})]
    (fx/merge cofx
              {:db (as-> db $
                     (assoc-in $ [:messages chat-id message-id :pinned?] pinned?)
                     (if pinned?
                       (-> $
                           (update-in [:pin-message-lists chat-id] message-list/add message)
                           (assoc-in [:pin-messages chat-id message-id] message))
                       (-> $
                           (update-in [:pin-message-lists chat-id] message-list/remove-message pin-message)
                           (update-in [:pin-messages chat-id] dissoc message-id))))}
              (data-store.messages/send-pin-message {:chat-id (pin-message :chat-id)
                                                     :message_id (pin-message :message-id)
                                                     :pinned (pin-message :pinned?)}))))

(fx/defn load-more-messages-for-current-chat
  {:events [:chat.ui/load-more-pin-messages-for-current-chat]}
  [{:keys [db] :as cofx}]
  (load-more-pin-messages cofx (:current-chat-id db) false))

(fx/defn load-pin-messages
  {:events [::load-pin-messages]}
  [{:keys [db now] :as cofx} chat-id]
  (when-not (get-in db [:pagination-info chat-id :pin-messages-initialized?])
    (fx/merge cofx
              {:db (assoc-in db [:pagination-info chat-id :pin-messages-initialized?] now)}
              (load-more-pin-messages chat-id true))))
