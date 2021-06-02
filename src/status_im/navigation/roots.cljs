(ns status-im.navigation.roots
  (:require [status-im.ui.components.colors :as colors]
            [status-im.utils.platform :as platform]))

(defn status-bar-options []
  (if platform/android?
    {:navigationBar {:backgroundColor colors/white}
     :statusBar     {:backgroundColor colors/white
                     :style           (if (colors/dark?) :light :dark)}}
    {:statusBar {:style (if (colors/dark?) :light :dark)}}))

(defn topbar-options []
  {:noBorder   true
   :elevation  0
   :background {:color colors/white}
   :backButton {:icon  (js/require "../resources/images/icons/arrow_left.png")
                :color colors/black}})

(defn bottom-tab-general []
  {:fontSize  11
   :iconColor colors/gray :selectedIconColor colors/blue
   :textColor colors/gray :selectedTextColor colors/blue})

(defn default-root []
  {:layout {:componentBackgroundColor colors/white
            :backgroundColor          colors/white}})

(defn roots []
  ;;TABS
  {:chat-stack
   {:root
    {:bottomTabs
     {:id       :tabs-stack
      :options  (merge (default-root)
                       {:bottomTabs {:titleDisplayMode :alwaysHide
                                     :backgroundColor  colors/white}})
      :children
      [;CHAT STACK
       {:stack {:id       :chat-stack
                :children [{:component {:name    :home
                                        :id      :home
                                        :options (merge (status-bar-options)
                                                        {:topBar (assoc (topbar-options) :visible false)})}}]
                :options  {:bottomTab (assoc (bottom-tab-general) :icon (js/require "../resources/images/icons/message.png"))}}}
       ;BROWSER STACK
       {:stack {:id       :browser-stack
                :children [{:component {:name    :empty-tab
                                        :id      :empty-tab
                                        :options (merge (status-bar-options)
                                                        {:topBar (assoc (topbar-options) :visible false)})}}]

                :options  {:bottomTab (assoc (bottom-tab-general) :icon (js/require "../resources/images/icons/browser.png"))}}}
       ;WALLET STACK
       {:stack {:id       :wallet-stack
                :children [{:component {:name    :wallet
                                        :id      :wallet
                                        :options (merge (status-bar-options)
                                                        {:topBar (assoc (topbar-options) :visible false)})}}]
                :options  {:bottomTab (assoc (bottom-tab-general) :icon (js/require "../resources/images/icons/wallet.png"))}}}
       ;STATUS STACK
       {:stack {:id       :status-stack
                :children [{:component {:name    :status
                                        :id      :status
                                        :options (merge (status-bar-options)
                                                        {:topBar (assoc (topbar-options) :visible false)})}}]
                :options  {:bottomTab (assoc (bottom-tab-general) :icon (js/require "../resources/images/icons/status.png"))}}}
       ;PROFILE STACK
       {:stack {:id       :profile-stack
                :children [{:component {:name    :my-profile
                                        :id      :my-profile
                                        :options (merge (status-bar-options)
                                                        {:topBar (assoc (topbar-options) :visible false)})}}]
                :options  {:bottomTab (assoc (bottom-tab-general) :icon (js/require "../resources/images/icons/user_profile.png"))}}}]}}}

   ;;INTRO (onboarding carousel)
   :intro
   {:root {:stack {:children [{:component {:name    :intro
                                           :id      :intro
                                           :options (status-bar-options)}}]
                   :options  (merge (default-root)
                                    {:topBar (assoc (topbar-options) :visible false)})}}}

   ;; ONBOARDING
   :onboarding
   {:root {:stack {:id       :onboarding
                   :children [{:component {:name    :get-your-keys
                                           :id      :get-your-keys
                                           :options (status-bar-options)}}]
                   :options  (merge (default-root)
                                    {:topBar (assoc (topbar-options) :elevation 0 :noBorder true)})}}}

   ;;PROGRESS
   :progress
   {:root {:stack {:children [{:component {:name    :progress
                                           :id      :progress
                                           :options (status-bar-options)}}]
                   :options  (merge (default-root)
                                    {:topBar (assoc (topbar-options) :visible false)})}}}

   ;;LOGIN
   :multiaccounts
   {:root {:stack {:id :multiaccounts-stack
                   :children [{:component {:name    :multiaccounts
                                           :id      :multiaccounts
                                           :options (status-bar-options)}}
                              {:component {:name    :login
                                           :id      :login
                                           :options (status-bar-options)}}]
                   :options  (merge (default-root)
                                    {:topBar (assoc (topbar-options) :visible false)})}}}

   ;;WELCOME
   :welcome
   {:root {:stack {:children [{:component {:name    :welcome
                                           :id      :welcome
                                           :options (status-bar-options)}}]
                   :options  (merge (default-root)
                                    {:topBar (assoc (topbar-options) :visible false)})}}}

   ;;NOTIFICATIONS
   :onboarding-notification
   {:root {:stack {:children [{:component {:name    :onboarding-notification
                                           :id      :onboarding-notification
                                           :options (status-bar-options)}}]
                   :options  (merge (default-root)
                                    {:topBar (assoc (topbar-options) :visible false)})}}}})