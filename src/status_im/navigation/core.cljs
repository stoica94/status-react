(ns status-im.navigation.core
  (:require
   ["react-native-navigation" :refer (Navigation)]
   ["react-native-gesture-handler" :refer (gestureHandlerRootHOC)]
   [status-im.ui.components.react :as react]
   [status-im.ui.components.colors :as colors]
   [status-im.reloader :as reloader]
   [re-frame.core :as re-frame]
   [status-im.utils.fx :as fx]
   [status-im.ui.screens.bottom-sheets.views :as bottom-sheets]
   [status-im.ui.screens.views :as views]
   [status-im.ui.screens.popover.views :as popover]
   [status-im.utils.platform :as platform]
   [reagent.core :as reagent]
   [status-im.navigation.roots :as roots]))

(def debug? ^boolean js/goog.DEBUG)

(defonce root-comp-id (atom nil))
(defonce pushed-screen-id (atom nil))
(defonce curr-modal (atom nil))
(defonce modals (atom []))

(defn update-title-options [options title]
  (if title
    (update options
            :topBar
            merge
            {:title {:text  title
                     :color colors/black}})
    options))

;; REGISTER COMPONENT (LAZY)
(defn reg-comp [key]
  (if-let [comp (get views/components (keyword key))]
    (.registerComponent Navigation key (fn [] (views/component comp)))
    (let [screen (views/screen key)]
      (.registerComponent Navigation key (fn [] (gestureHandlerRootHOC screen)) (fn [] screen)))))

(defonce rset-lazy-reg
  (.setLazyComponentRegistrator Navigation reg-comp))

;; PUSH SCREEN
(defn navigate [comp]
  (let [{:keys [options title]} (get views/screens comp)]
    (println "NAVIGATE" comp " cur-root: " (name @root-comp-id))
    (.push Navigation
           (name @root-comp-id)
           (clj->js {:component {:id      comp
                                 :name    comp
                                 :options (update-title-options (merge options
                                                                       (roots/status-bar-options)
                                                                       {:topBar (merge (:topBar options)
                                                                                       (roots/topbar-options))})
                                                                title)}}))
    ;;if we push the screen from modal, we want to dismiss all modals
    (when @curr-modal
      (reset! curr-modal false)
      (reset! modals [])
      (.dismissAllModals Navigation))))

;; OPEN MODAL
(defn update-modal-topbar-options [options title]
  (update (update-title-options options title)
          :topBar
          merge
          {:elevation       0
           :noBorder        true
           :leftButtonColor colors/black
           :leftButtons     {:id   "dismiss-modal"
                             :icon (js/require "../resources/images/icons/close.png")}}))

(re-frame/reg-fx
 :open-modal-fx
 (fn [comp]
   (let [{:keys [options title]} (get views/screens comp)]
     (reset! curr-modal true)
     (swap! modals conj comp)
     (println "OPEN MODAL" (count @modals))
     (.showModal Navigation
                 (clj->js {:stack {:children
                                   [{:component
                                     {:name    comp
                                      :id      comp
                                      :options (update-modal-topbar-options
                                                (merge (roots/status-bar-options)
                                                       (roots/default-root)
                                                       options)
                                                title)}}]}})))))

;; DISSMISS MODAL
(defn dissmissModal []
  (.dismissModal Navigation (name (last @modals))))

(defonce register-nav-button-reg
  (.registerNavigationButtonPressedListener
   (.events Navigation)
   (fn [^js evn]
     (when (= "dismiss-modal" (.-buttonId evn))
       (dissmissModal)))))

(defonce register-modal-reg
  (.registerModalDismissedListener
   (.events Navigation)
   (fn [_]
     (println "CLOSE MODAL" (count @modals))
     (if (> (count @modals) 1)
       (let [new-modals (butlast @modals)]
         (reset! modals (vec new-modals))
         (re-frame/dispatch [:set :view-id (last new-modals)]))
       (do
         (reset! modals [])
         (reset! curr-modal false)
         (re-frame/dispatch [:set :view-id @pushed-screen-id]))))))

;; SCREEN APPEAR
(defonce screen-appear-reg
  (.registerComponentDidAppearListener
   (.events Navigation)
   (fn [^js evn]
     (let [view-id (keyword (.-componentName evn))]
       (when (and (not= view-id :bottom-sheet) (not= view-id :popover) (get views/screens view-id))
         (re-frame/dispatch [:set :view-id view-id])
         (when-not @curr-modal
           (reset! pushed-screen-id view-id)))))))

;; SET ROOT
(re-frame/reg-fx
 :init-root-fx
 (fn [root-id]
   (reset! root-comp-id root-id)
   (.setRoot Navigation (clj->js (get (roots/roots) root-id)))))

;; SET STACK ROOT
(re-frame/reg-fx
 :rnn-set-root-fx
 (fn [comp]
   (let [{:keys [options title]} (get views/screens comp)]
     (.setStackRoot Navigation
                    "browser-stack"
                    (clj->js {:component {:id      comp
                                          :name    comp
                                          :options (update-title-options options title)}})))))

;; POPOVER
(def popover-comp
  (reagent/reactify-component
   (fn []
     ^{:key (str @colors/theme @reloader/cnt)}
     [react/safe-area-provider
      [popover/popover]
      (when debug?
        [reloader/reload-view])])))

(defonce popover-reg
  (.registerComponent Navigation
                      "popover"
                      (fn [] (gestureHandlerRootHOC popover-comp))
                      (fn [] popover-comp)))

(re-frame/reg-fx
 :rnn-show-popover
 (fn []
   (.showOverlay Navigation
                 (clj->js
                  {:component {:name    "popover"
                               :options (merge (if platform/android?
                                                 {:statusBar {:translucent true}}
                                                 (roots/status-bar-options))
                                               {:layout  {:componentBackgroundColor "transparent"}
                                                :overlay {:interceptTouchOutside true}})}}))))

(re-frame/reg-fx
 :rnn-hide-popover
 (fn []
   (.dismissAllOverlays Navigation)))

;; BOTTOM SHEETS
(def sheet-comp
  (reagent/reactify-component
   (fn []
     ^{:key (str @colors/theme @reloader/cnt)}
     [react/safe-area-provider
      [bottom-sheets/bottom-sheet]
      (when debug?
        [reloader/reload-view])])))

(defonce bottom-sheet-reg
  (.registerComponent Navigation
                      "bottom-sheet"
                      (fn [] (gestureHandlerRootHOC sheet-comp))
                      (fn [] sheet-comp)))

(re-frame/reg-fx
 :rnn-show-bottom-sheet
 (fn []
   (.showOverlay Navigation
                 (clj->js
                  {:component {:name    "bottom-sheet"
                               :options (merge (if platform/android?
                                                 {:statusBar {:translucent true}}
                                                 (roots/status-bar-options))
                                               {:layout  {:componentBackgroundColor "transparent"}
                                                :overlay {:interceptTouchOutside true}})}}))))

(re-frame/reg-fx
 :rnn-hide-bottom-sheet
 (fn []
   (.dismissAllOverlays Navigation)))

;; BOTTOM TABS
(def tab-root-ids {0 :chat-stack
                   1 :browser-stack
                   2 :wallet-stack
                   3 :status-stack
                   4 :profile-stack})

(def tab-key-idx {:chat    0
                  :browser 1
                  :wallet  2
                  :status  3
                  :profile 4})

(re-frame/reg-fx
 :rnn-change-tab-fx
 (fn [tab]
   (reset! root-comp-id (get tab-root-ids (get tab-key-idx tab)))
   (.mergeOptions Navigation "tabs-stack" (clj->js {:bottomTabs {:currentTabIndex (get tab-key-idx tab)}}))))

(re-frame/reg-fx
 :rnn-pop-to-root-tab-fx
 (fn [comp]
   (.popToRoot Navigation (name comp))))

(defonce register-bottom-tab-reg
  (.registerBottomTabSelectedListener
   (.events Navigation)
   (fn [^js evn]
     (reset! root-comp-id (get tab-root-ids (.-selectedTabIndex evn))))))

;; NAVIGATION

(fx/defn init-root
  {:events [:init-root]}
  [_ root-id]
  {:init-root-fx root-id})

(re-frame/reg-fx
 :rnn-navigate-to-fx
 (fn [key]
   ;;TODO WHY #{:home} ?
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
