(ns status-im.navigation.core
  (:require
   [status-im.ui.components.react :as react]
   [status-im.ui.components.colors :as colors]
   [status-im.reloader :as reloader]
   [re-frame.core :as re-frame]
   [status-im.utils.fx :as fx]
   [status-im.ui.screens.bottom-sheets.views :as bottom-sheets]
   [status-im.ui.screens.views :as views]
   [status-im.ui.screens.popover.views :as popover]
   ["react-native-navigation" :refer (Navigation)]
   ["react-native-gesture-handler" :refer (gestureHandlerRootHOC)]
   [status-im.utils.platform :as platform]
   [reagent.core :as reagent]))

(def debug? ^boolean js/goog.DEBUG)

(defonce root-comp-id (atom nil))
(defonce curr-id (atom nil))
(defonce curr-modal (atom nil))
(defonce modals (atom []))

(defn set-current-screen [view-id]
  (when (and (not= view-id :bottom-sheet) (not= view-id :popover) (get views/screens view-id))
    (re-frame/dispatch [:set :view-id view-id])
    (when-not @curr-modal
      (reset! curr-id view-id))))

(defn reg-comp [key]
  (println "REG COMP" key)
  (if-let [comp (get views/components (keyword key))]
    (.registerComponent Navigation key (fn [] (reagent/reactify-component (fn [] [react/view {:width 500 :height 44}
                                                                                  [comp]]))))
    (let [screen (views/screen key)]
      (.registerComponent Navigation key (fn [] (gestureHandlerRootHOC screen)) (fn [] screen)))))

(.setLazyComponentRegistrator Navigation reg-comp)

(defn status-bar-options []
  (if platform/android?
    {:navigationBar {:backgroundColor colors/white}
     :statusBar
     {:backgroundColor colors/white
      :style (if (colors/dark?) :light :dark)}}
    {:statusBar {:style (if (colors/dark?) :light :dark)}}))

(defn general-options [] {:topBar {:noBorder   true
                                   :elevation  0
                                   :background {:color colors/white}
                                   :backButton {:icon  (js/require "../resources/images/icons/arrow_left.png")
                                                :color colors/black}}})

(re-frame/reg-fx
 :rnn-set-root-fx
 (fn [comp]
   (let [{:keys [options title]} (get views/screens comp)]
     (.setStackRoot Navigation "browser-stack" (clj->js {:component {:id      comp
                                                                     :name    comp
                                                                     :options (merge (status-bar-options) (when title {:topBar {:title {:text title :color colors/black}}}) options)}})))))

(re-frame/reg-fx
 :open-modal-fx
 (fn [comp]
   (let [{:keys [options title]} (get views/screens comp)]
     (reset! curr-modal true)
     (swap! modals conj comp)
     (.showModal Navigation (clj->js {:stack
                                      {:children
                                       [{:component
                                         {:name    comp
                                          :id comp
                                          :options (merge (status-bar-options)
                                                          {:topBar
                                                           (merge
                                                            (when title {:title {:text title :color colors/black}})
                                                            {:elevation       0
                                                             :noBorder        true
                                                             :leftButtonColor colors/black
                                                             :leftButtons
                                                             {:id   "dismiss-modal"
                                                              :icon (js/require "../resources/images/icons/close.png")}})}
                                                          options)}}]}})))))

(defn navigate [comp]
  (let [{:keys [options title]} (get views/screens comp)]
    (.push Navigation
           (name @root-comp-id)
           (clj->js {:component {:id      comp
                                 :name    comp
                                 :options (merge (status-bar-options) (general-options) (update options :topBar merge (when title {:title {:text title :color colors/black}})))}}))
    (when @curr-modal
      (reset! curr-modal false)
      (reset! modals [])
      (.dismissAllModals Navigation))))

(.registerComponentDidAppearListener
 (.events Navigation)
 (fn [^js evn]
   (set-current-screen (keyword (.-componentName evn)))
   (println "DID APPEAR"
            (.-componentId evn)
            (.-componentName evn))))

(.registerComponentDidDisappearListener
 (.events Navigation)
 (fn [^js evn]
   (println "DID DISAPPEAR"
            (.-componentId evn)
            (.-componentName evn))))

(defn dissmissModal []
  (.dismissModal Navigation (name (last @modals))))

(.registerNavigationButtonPressedListener
 (.events Navigation)
 (fn [^js evn]
   (when (= "dismiss-modal" (.-buttonId evn))
     (dissmissModal))))

(.registerModalDismissedListener
 (.events Navigation)
 (fn [_]
   (println "..registerModalDismissedListener"
            (butlast @modals)
            @curr-id)
   (if (> (count @modals) 1)
     (let [new-modals (butlast @modals)]
       (reset! modals (vec new-modals))
       (re-frame/dispatch [:set :view-id (last new-modals)]))
     (do
       (reset! modals [])
       (reset! curr-modal false)
       (re-frame/dispatch [:set :view-id @curr-id])))))

;; POPOVER

(def popover-comp
  (reagent/reactify-component
   (fn []
     ^{:key (str @colors/theme @reloader/cnt)}
     [react/safe-area-provider
      [popover/popover]
      (when debug?
        [reloader/reload-view])])))

(.registerComponent Navigation
                    "popover"
                    (fn [] (gestureHandlerRootHOC popover-comp))
                    (fn [] popover-comp))

(re-frame/reg-fx
 :rnn-show-popover
 (fn []
   (.showOverlay Navigation
                 (clj->js
                  {:component {:name    "popover"
                               :options (merge (status-bar-options)
                                               {:layout    {:componentBackgroundColor "transparent"}
                                                :overlay   {:interceptTouchOutside true}})}}))))

(re-frame/reg-fx
 :rnn-hide-popover
 (fn []
   (.dismissAllOverlays Navigation)))

;; BOTTOM SHEETS

;; register bottom-sheet component

(def sheet-comp
  (reagent/reactify-component
   (fn []
     ^{:key (str @colors/theme @reloader/cnt)}
     [react/safe-area-provider
      [bottom-sheets/bottom-sheet]
      (when debug?
        [reloader/reload-view])])))

(.registerComponent Navigation
                    "bottom-sheet"
                    (fn [] (gestureHandlerRootHOC sheet-comp))
                    (fn [] sheet-comp))

(re-frame/reg-fx
 :rnn-show-bottom-sheet
 (fn []
   (.showOverlay Navigation
                 (clj->js
                  {:component {:name    "bottom-sheet"
                               :options (merge (status-bar-options)
                                               {:layout    {:componentBackgroundColor "transparent"}
                                                :overlay   {:interceptTouchOutside true}})}}))))

(re-frame/reg-fx
 :rnn-hide-bottom-sheet
 (fn []
   (.dismissAllOverlays Navigation)))

;; ROOT STACKS

(defn set-root [root id]
  (reset! root-comp-id id)
  (.setRoot Navigation (clj->js root)))

;;this stack has only one screen with carusel, and showed only once when user installed the app
(re-frame/reg-fx
 :init-intro-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name :intro
                                                     :id :intro
                                                     :options   (status-bar-options)}}]
                             :options  {:topBar {:visible false}}}}}
             nil)))

(re-frame/reg-fx
 :init-onboarding-notification-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name    :notifications-onboarding
                                                     :id :notifications-onboarding
                                                     :options (status-bar-options)}}]
                             :options  {:topBar {:visible false}}}}}
             nil)))

(re-frame/reg-fx
 :init-welcome-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name    :welcome
                                                     :id :welcome
                                                     :options (status-bar-options)}}]
                             :options  {:topBar {:visible false}}}}}
             nil)))

(re-frame/reg-fx
 :init-progress-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name    :progress
                                                     :id :progress
                                                     :options (status-bar-options)}}]
                             :options  {:topBar {:visible false}}}}}
             nil)))

(re-frame/reg-fx
 :init-login-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name    :multiaccounts
                                                     :id      :login-multiaccounts
                                                     :options (merge (status-bar-options) {:topBar {:elevation 0
                                                                                                    :visible   false}})}}
                                        {:component {:name    :login
                                                     :options (merge (status-bar-options) {:topBar {:elevation 0
                                                                                                    :visible   false}})}}]
                             :options  (general-options)}}}
             :login-multiaccounts)))

(def tab-root-ids {0 :home-root
                   1 :browser-root
                   2 :wallet-root
                   3 :status-root
                   4 :profile-root})

(def tab-key-idx {:chat 0
                  :browser 1
                  :wallet 2
                  :status 3
                  :profile 4})

(re-frame/reg-fx
 :rnn-change-tab-fx
 (fn [tab]
   (reset! root-comp-id (get tab-root-ids (get tab-key-idx tab)))
   (.mergeOptions Navigation "tabs-stack" (clj->js {:bottomTabs {:currentTabIndex (get tab-key-idx tab)}}))))

(re-frame/reg-fx
 :rnn-pop-to-root-tab-fx
 (fn [comp]
   (println "POP TO ROOT" (name comp))
   (.popToRoot Navigation (name comp))))

(.registerBottomTabSelectedListener
 (.events Navigation)
 (fn [^js evn]
   (println "selectedTabIndex" (.-selectedTabIndex evn))
   (reset! root-comp-id (get tab-root-ids (.-selectedTabIndex evn)))))

(defn bottom-tab-general []
  {:fontSize  11
   :iconColor colors/gray :selectedIconColor colors/blue
   :textColor colors/gray :selectedTextColor colors/blue})

(re-frame/reg-fx
 :init-tabs-fx
 (fn []
   (set-root {:root {:bottomTabs
                     {:id :tabs-stack
                      :options {:layout {:componentBackgroundColor colors/white}
                                :bottomTabs {:titleDisplayMode :alwaysHide
                                             :backgroundColor colors/white}}

                      :children
                      [{:stack {:id :chat-stack
                                :children [{:component {:name    :home
                                                        :id      :home-root
                                                        :options (merge (status-bar-options) {:topBar {:visible false}})}}]
                                :options  (merge (general-options)
                                                 ;;TAB
                                                 {:bottomTab (merge (bottom-tab-general)
                                                                    {;:text (i18n/label :t/chat)
                                                                     :icon (js/require "../resources/images/icons/message.png")})})}}
                       {:stack {:id :browser-stack
                                :children [{:component {:name    :empty-tab
                                                        :id      :browser-root
                                                        :options (merge (status-bar-options) {:topBar {:visible false}})}}]

                                :options  (merge (general-options)
                                                 ;;TAB
                                                 {:bottomTab (merge (bottom-tab-general)
                                                                    {;:text (i18n/label :t/browser)
                                                                     :icon (js/require "../resources/images/icons/browser.png")})})}}
                       {:stack {:id :wallet-stack
                                :children [{:component {:name    :wallet
                                                        :id      :wallet-root
                                                        :options (merge (status-bar-options) {:topBar    {:visible false}})}}]
                                :options  (merge (general-options)
                                                 ;;TAB
                                                 {:bottomTab (merge (bottom-tab-general) {;:text (i18n/label :t/wallet)
                                                                                          :icon (js/require "../resources/images/icons/wallet.png")})})}}
                       {:stack {:id :status-stack
                                :children [{:component {:name    :status
                                                        :id      :status-root
                                                        :options (merge (status-bar-options) {:topBar    {:visible false}})}}]
                                :options  (merge (general-options)
                                                 ;;TAB
                                                 {:bottomTab (merge (bottom-tab-general) {;:text (i18n/label :t/status)
                                                                                          :icon (js/require "../resources/images/icons/status.png")})})}}
                       {:stack {:id :profile-stack
                                :children [{:component {:name    :my-profile
                                                        :id      :profile-root
                                                        :options (merge (status-bar-options) {:topBar    {:visible false}})}}]
                                :options  (merge (general-options)
                                                 ;;TAB
                                                 {:bottomTab (merge (bottom-tab-general) {;:text (i18n/label :t/profile)
                                                                                          :icon (js/require "../resources/images/icons/user_profile.png")})})}}]}}}
             :home-root)))

;;this stack for onboarding navigation, showed only after intro stack
(re-frame/reg-fx
 :init-onboarding-fx
 (fn []
   (set-root {:root {:stack {:children [{:component {:name    :get-your-keys
                                                     :id      :onboarding-root-component
                                                     :options (merge (status-bar-options) {:topBar {:elevation 0
                                                                                                    :noBorder  true}})}}]
                             :options (general-options)}}}
             :onboarding-root-component)))

(fx/defn init-intro
  {:events [::init-intro]}
  [cofx]
  {:init-intro-fx nil})

(fx/defn init-progress
  {:events [:init-progress]}
  [cofx]
  {:init-progress-fx nil})

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

(fx/defn init-tabs
  {:events [:init-tabs]}
  [_]
  {:init-tabs-fx nil})

;; NAVIGATION

(re-frame/reg-fx
 :rnn-navigate-to-fx
 (fn [key]
   (when-not (#{:home} key)
     (navigate key))))

(re-frame/reg-fx
 :rnn-navigate-back-fx
 (fn []
   (if @curr-modal
     (dissmissModal)
     (.pop Navigation (name @root-comp-id)))))

(fx/defn rnn-navigate-to
  {:events [:rnn-navigate-to]}
  [_ key]
  {:rnn-navigate-to-fx key})

(fx/defn rnn-navigate-back
  {:events [:rnn-navigate-back]}
  [_]
  {:rnn-navigate-back-fx nil})

(re-frame/reg-fx
 :navigate-replace-fx
 (fn [[view-id _]]
   (.pop Navigation (name @root-comp-id))
   (navigate view-id)))
