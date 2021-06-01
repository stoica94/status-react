(ns status-im.navigation
  (:require [re-frame.core :as re-frame]
            [taoensso.timbre :as log]
            [status-im.utils.fx :as fx]
            [status-im.anon-metrics.core :as anon-metrics]))
;;TODO
(re-frame/reg-fx
 ::navigate-reset
 (fn [config]
   (log/debug :navigate-reset config)))
   ;(navigation/navigate-reset config)))

;;TODO
(fx/defn navigate-reset
  {:events [:navigate-reset]}
  [_ config]
  {::navigate-reset config})

(defn- all-screens-params [db view screen-params]
  (cond-> db
    (and (seq screen-params) (:screen screen-params) (:params screen-params))
    (all-screens-params (:screen screen-params) (:params screen-params))

    (seq screen-params)
    (assoc-in [:navigation/screen-params view] screen-params)))

(fx/defn navigate-to-cofx
  [{:keys [db] :as cofx} go-to-view-id screen-params]
  {:db
   (-> (assoc db :view-id go-to-view-id)
       (all-screens-params go-to-view-id screen-params))
   ;::navigate-to [go-to-view-id screen-params]
   :rnn-navigate-to-fx              go-to-view-id
   ;; simulate a navigate-to event so it can be captured be anon-metrics
   ::anon-metrics/transform-and-log {:coeffects {:event [:navigate-to go-to-view-id screen-params]}}})

(fx/defn navigate-to
  {:events [:navigate-to]}
  [cofx go-to-view-id screen-params]
  (navigate-to-cofx cofx go-to-view-id screen-params))

(fx/defn navigate-back
  {:events [:navigate-back]}
  [_]
  {:rnn-navigate-back-fx nil})

(fx/defn pop-to-root-tab
  {:events [:pop-to-root-tab]}
  [_ tab]
  {:rnn-pop-to-root-tab-fx tab})

(fx/defn set-root
  {:events [:navigate-set-root]}
  [_ root]
  {:rnn-set-root-fx root})

(fx/defn change-tab
  {:events [:navigate-change-tab]}
  [_ tab]
  {:rnn-change-tab-fx tab})

(fx/defn navigate-replace
  {:events       [:navigate-replace]
   :interceptors [anon-metrics/interceptor]}
  [{:keys [db]} go-to-view-id screen-params]
  (let [db (cond-> (assoc db :view-id go-to-view-id)
             (seq screen-params)
             (assoc-in [:navigation/screen-params go-to-view-id] screen-params))]
    {:db                  db
     :navigate-replace-fx [go-to-view-id screen-params]}))

(fx/defn open-modal
  {:events [:open-modal]}
  [{:keys [db]} comp screen-params]
  {:db
   (-> (assoc db :view-id comp)
       (all-screens-params comp screen-params))
   :open-modal-fx comp})
