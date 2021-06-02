(ns status-im.ui.components.tabbar.core
  (:require [re-frame.core :as re-frame]))

(defn chat-tab []
  (let [count-subscription @(re-frame/subscribe [:chats/unread-messages-number])]
    (println "CHAT TAB" count-subscription)
    (re-frame/dispatch [:change-tab-count :chat count-subscription])
    nil))

(defn profile-tab []
  (let [count-subscription @(re-frame/subscribe [:get-profile-unread-messages-number])]
    (println "PROFILE TAB" count-subscription)
    (re-frame/dispatch [:change-tab-count :profile count-subscription])
    nil))

(defn tabs-counts-subscriptions []
  [:<>
   [chat-tab]
   [profile-tab]])