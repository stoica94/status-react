(ns status-im.navigation.core
  (:require ["react-native-navigation" :refer (Navigation)]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.colors :as colors]
            [status-im.reloader :as reloader]
            [status-im.ui.screens.routing.core :as routing]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [status-im.utils.fx :as fx]
            [status-im.i18n.i18n :as i18n]
            [status-im.ui.screens.bottom-sheets.views :as bottom-sheets]
            [status-im.ui.screens.views :as views]))

(def debug? ^boolean js/goog.DEBUG)

(defonce root-comp-id (atom nil))

(defn reg-comp [key]
  (.registerComponent Navigation key (views/screen key)))

(.setLazyComponentRegistrator Navigation reg-comp)

(defn navigate [comp]
  (.push Navigation
         (name @root-comp-id)
         (clj->js {:component {:name    (name comp)
                               :options (get-in views/screens [comp :options])}})))

;; BOTTOM SHEETS

;; register bottom-sheet component

(.registerComponent Navigation
                    "bottom-sheet"
                    (fn []
                      (reagent.core/reactify-component
                       (fn []
                         ^{:key (str @colors/theme @reloader/cnt)}
                         [react/safe-area-provider
                          [bottom-sheets/bottom-sheet]
                          (when debug?
                            [reloader/reload-view])]))))

(re-frame/reg-fx
 :rnn-show-bottom-sheet
 (fn []
   (.showOverlay Navigation
                 (clj->js
                  {:component {:name    "bottom-sheet"
                               :options {:layout  {:componentBackgroundColor "transparent"}
                                         :overlay {:interceptTouchOutside true}}}}))))

(re-frame/reg-fx
 :rnn-hide-bottom-sheet
 (fn []
   (.dismissAllOverlays Navigation)))

;; ROOT STACKS

(defn set-root [root id]
  (reset! root-comp-id id)
  (.setRoot Navigation (clj->js root)))

(def general-options {:topBar {:noBorder   true
                               :backButton {:icon  (js/require "../resources/images/icons/arrow_left.png")
                                            :color :black}}})

;;(def progress-root {:root {:stack {:children [{:component {:name :progress}}]}}})

;;this stack has only one screen with carusel, and showed only once when user installed the app
(re-frame/reg-fx
 :init-intro-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name :intro}}]
                             :options  {:topBar {:visible false}}}}}
             nil)))

(re-frame/reg-fx
 :init-onboarding-notification-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name :notifications-onboarding}}]
                             :options  {:topBar {:visible false}}}}}
             nil)))

(re-frame/reg-fx
 :init-welcome-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name :welcome}}]
                             :options  {:topBar {:visible false}}}}}
             nil)))

(re-frame/reg-fx
 :init-login-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name :multiaccounts
                                                     :id      :login-multiaccounts
                                                     :options  {:topBar {:visible false}}}}
                                        {:component {:name :login}}]
                             :options  general-options}}}
             :login-multiaccounts)))

(def tab-root-ids {0 :home-root
                   1 :browser-root
                   2 :wallet-root
                   3 :status-root
                   4 :profile-root})

(.registerBottomTabSelectedListener (.events Navigation)
                                    (fn [evn]
                                      (println "selectedTabIndex" (.-selectedTabIndex evn))
                                      (reset! root-comp-id (get tab-root-ids (.-selectedTabIndex evn)))))

(re-frame/reg-fx
 :init-tabs-fx
 (fn []
   (set-root {:root {:bottomTabs
                     {:options {;:bottomTabs {:elevation 0
                                             ;:hideShadow true
                                :bottomTab {:iconWidth 20 :iconHeight 20 :fontSize 11
                                            :iconColor colors/gray :selectedIconColor colors/blue
                                            :textColor colors/gray :selectedTextColor colors/blue}}
                      :children
                      [{:stack {:children [{:component {:name :home
                                                        :id :home-root
                                                        :options  {:topBar {:visible false}}}}]
                                :options (merge general-options
                                                ;;TAB
                                                {:bottomTab {:text (i18n/label :t/chat)
                                                             :icon  (js/require "../resources/images/icons/message.png")}})}}
                       {:stack {:children [{:component {:name :empty-tab
                                                        :id :browser-root
                                                        :options  {:topBar {:visible false}}}}]
                                :options (merge general-options
                                                ;;TAB
                                                {:bottomTab {:text (i18n/label :t/browser)
                                                             :icon  (js/require "../resources/images/icons/browser.png")}})}}
                       {:stack {:children [{:component {:name :wallet
                                                        :id :wallet-root
                                                        :options  {:topBar {:visible false}}}}]
                                :options (merge general-options
                                                ;;TAB
                                                {:bottomTab {:text (i18n/label :t/wallet)
                                                             :icon  (js/require "../resources/images/icons/wallet.png")}})}}
                       {:stack {:children [{:component {:name :status
                                                        :id :status-root
                                                        :options  {:topBar {:visible false}}}}]
                                :options (merge general-options
                                                ;;TAB
                                                {:bottomTab {:text (i18n/label :t/status)
                                                             :icon  (js/require "../resources/images/icons/status.png")}})}}
                       {:stack {:children [{:component {:name :my-profile
                                                        :id :profile-root
                                                        :options  {:topBar {:visible false}}}}]
                                :options (merge general-options
                                                ;;TAB
                                                {:bottomTab {:text (i18n/label :t/profile)
                                                             :icon  (js/require "../resources/images/icons/user_profile.png")}})}}]}}}
             :home-root)))

;;this stack for onboarding navigation, showed only after intro stack
(re-frame/reg-fx
 :init-onboarding-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name    :get-your-keys
                                                     :id      :onboarding-root-component
                                                     :options {:topBar {:noBorder true}}}}]
                             :options  general-options}}}
             :onboarding-root-component)))

(fx/defn init-intro
  {:events [::init-intro]}
  [cofx]
  {:init-intro-fx nil})

(fx/defn init-onboarding
  {:events [:init-onboarding]}
  [_]
  {:init-onboarding-fx nil})

(fx/defn init-onboarding-notification
  {:events [:init-onboarding-notification]}
  [_]
  {:init-onboarding-notification-fx nil})

(fx/defn init-welcome
  {:events [:init-welcome]}
  [_]
  {:init-welcome-fx nil})

(fx/defn init-login
  {:events [:init-login]}
  [_]
  {:init-login-fx nil})

;; NAVIGATION

(re-frame/reg-fx
 :rnn-navigate-to-fx
 (fn [key]
   (when-not (#{:home} key)
     (navigate key))))

(re-frame/reg-fx
 :rnn-navigate-back-fx
 (fn []
   (.pop Navigation (name @root-comp-id))))

(fx/defn rnn-navigate-to
  {:events [:rnn-navigate-to]}
  [_ key]
  {:rnn-navigate-to-fx key})

(fx/defn rnn-navigate-back
  {:events [:rnn-navigate-back]}
  [_]
  {:rnn-navigate-back-fx nil})