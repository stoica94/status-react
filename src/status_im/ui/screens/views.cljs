(ns status-im.ui.screens.views
  (:require [status-im.ui.components.react :as react]
            [reagent.core :as reagent]
            [status-im.ui.components.colors :as colors]
            [status-im.reloader :as reloader]
            [status-im.ui.screens.routing.intro-login-stack :as intro-login-stack]
            [status-im.ui.screens.routing.chat-stack :as chat-stack]
            [status-im.ui.screens.routing.wallet-stack :as wallet-stack]
            [status-im.ui.screens.routing.profile-stack :as profile-stack]
            [status-im.ui.screens.routing.browser-stack :as browser-stack]
            [status-im.ui.screens.routing.status-stack :as status-stack]
            [status-im.ui.screens.routing.core :as routing]))

(defn get-screens []
  (reduce
   (fn [acc {:keys [name component options insets]}]
     (assoc acc name {:component component :options options :insets insets}))
   {}
   (concat intro-login-stack/screens
           chat-stack/screens
           wallet-stack/screens
           profile-stack/screens
           ;;TODO change navigation for browser, change root , don't push
           browser-stack/screens
           status-stack/screens)))

;;TODO find why hot reload doesn't work
(def screens (get-screens))

(defn screen [key]
  (reagent.core/reactify-component
   (fn []
     ^{:key (str @colors/theme @reloader/cnt)}
     [react/safe-area-provider
      [react/safe-area-consumer
       (fn [insets]
         (reagent/as-element
          [react/view {;;TODO check how it works
                       :style (routing/wrapped-screen-style
                               {:insets (get-in screens [(keyword key) :insets])}
                               insets)}
           [(get-in (if js/goog.DEBUG (get-screens) screens) [(keyword key) :component])]]))]
      (when js/goog.DEBUG
        [reloader/reload-view])])))