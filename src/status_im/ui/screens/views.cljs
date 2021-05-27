(ns status-im.ui.screens.views
  (:require [status-im.ui.components.react :as react]
            [reagent.core :as reagent]
            [status-im.ui.components.colors :as colors]
            [status-im.reloader :as reloader]
            [status-im.ui.screens.screens :as screens]
            [status-im.ui.screens.routing.core :as routing]))

(defn get-screens []
  (reduce
   (fn [acc screen]
     (assoc acc (:name screen) screen))
   {}
   screens/screens))

;;TODO find why hot reload doesn't work
(def screens (get-screens))

(def components
  (reduce
   (fn [acc {:keys [name component]}]
     (assoc acc name component))
   {}
   (concat screens/components)))

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