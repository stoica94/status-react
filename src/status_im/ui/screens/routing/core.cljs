(ns status-im.ui.screens.routing.core
  (:require [oops.core :refer [oget]]))

;; TODO(Ferossgp): Unify with topbar back icon. Maybe dispatch the same event and move the all logic inside the event.
#_(defn handle-on-screen-focus
    [{:keys [back-handler on-focus name]}]
    (use-focus-effect
     (use-callback
      (fn []
        (log/debug :on-screen-focus name)
        (let [on-back-press (fn []
                              (if (fn? back-handler)
                                (back-handler)
                                (do
                                  (when (and back-handler
                                             (vector? back-handler)
                                             (not= back-handler :noop))
                                    (re-frame/dispatch back-handler))
                                  (boolean back-handler))))]
          (when on-focus (re-frame/dispatch on-focus))
          (add-back-handler-listener on-back-press)
          (fn []
            (remove-back-handler-listener on-back-press))))
      #js [])))

#_(defn handle-on-screen-blur [navigation]
    (use-effect
     (fn []
       (ocall navigation "addListener" "blur"
              (fn []
                ;; Reset currently mounted text inputs to their default values
                ;; on navigating away; this is a privacy measure
                (doseq [[_ {:keys [ref value]}] @quo/text-input-refs]
                  (.setNativeProps ^js ref (clj->js {:text value})))
                (doseq [[^js text-input default-value] @react/text-input-refs]
                  (.setNativeProps text-input (clj->js {:text default-value}))))))
     #js [navigation]))

(defn wrapped-screen-style [{:keys [insets style]} insets-obj]
  (merge
   {:flex 1}
   style
   (when (get insets :bottom)
     {:padding-bottom (+ (oget insets-obj "bottom")
                         (get style :padding-bottom)
                         (get style :padding-vertical))})
   (when (get insets :top true)
     {:padding-top (+ (oget insets-obj "top")
                      (get style :padding-top)
                      (get style :padding-vertical))})))

#_(defn wrap-screen [{:keys [component] :as options}]
    (assoc options :component
           (fn [props]
             (handle-on-screen-blur
              (oget props "navigation"))
             (handle-on-screen-focus options)
             (let [props'   (js->clj props :keywordize-keys true)
                   focused? (oget props "navigation" "isFocused")]
               (reagent/as-element
                [react/safe-area-consumer
                 (fn [insets]
                   (reagent/as-element
                    [react/view {:style (wrapped-screen-style options insets)}
                     [component props' (focused?)]]))])))))