(ns status-im.ui.screens.intro.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [status-im.constants :as constants]
            [status-im.i18n.i18n :as i18n]
            [status-im.multiaccounts.create.core :refer [step-kw-to-num]]
            [status-im.react-native.resources :as resources]
            [status-im.ui.components.colors :as colors]
            [status-im.ui.components.icons.icons :as icons]
            [status-im.ui.components.radio :as radio]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.topbar :as topbar]
            [status-im.ui.screens.intro.styles :as styles]
            [status-im.ui.components.toolbar :as toolbar]
            [status-im.utils.gfycat.core :as gfy]
            [status-im.utils.identicon :as identicon]
            [status-im.utils.security :as security]
            [status-im.utils.debounce :refer [dispatch-and-chill]]
            [quo.core :as quo]
            [status-im.utils.utils :as utils]
            [status-im.utils.datetime :as datetime])
  (:require-macros [status-im.utils.views :refer [defview letsubs]]))

(defn bottom-bar [{:keys [step weak-password?
                          forward-action
                          next-button-disabled?
                          processing? existing-account?]}]
  [react/view {:style {:align-items :center}}
   (cond (and (#{:generate-key :recovery-success} step) processing?)
         [react/view {:min-height 46 :max-height 46 :align-self :stretch :margin-bottom 16}
          [react/activity-indicator {:animating true
                                     :size      :large}]]
         (#{:generate-key :recovery-success} step)
         (let [label-kw (case step
                          :generate-key     :t/generate-a-key
                          :recovery-success :t/re-encrypt-key
                          :intro-wizard-title6)]
           [react/view {:style (assoc styles/bottom-button :margin-bottom 16)}
            [quo/button
             {:disabled            existing-account?
              :on-press            #(re-frame/dispatch [forward-action])
              :accessibility-label :onboarding-next-button}
             (i18n/label label-kw)]])
         :else
         [toolbar/toolbar
          {:show-border? true
           :right        [quo/button
                          {:on-press            #(dispatch-and-chill [forward-action] 300)
                           :accessibility-label :onboarding-next-button
                           :disabled            (or processing?
                                                    (and (= step :create-code) weak-password?)
                                                    (and (= step :enter-phrase) next-button-disabled?))
                           :type                :secondary
                           :after               :main-icons/next}
                          (i18n/label :t/next)]}])
   (when (and (= :generate-key step) (not processing?))
     [react/view {:padding-vertical 8}
      [quo/button
       {:on-press #(re-frame/dispatch
                    [:multiaccounts.recover.ui/recover-multiaccount-button-pressed])
        :type     :secondary}
       (i18n/label :t/access-existing-keys)]])
   (when (or (= :generate-key step) (and processing? (= :recovery-success step)))
     [react/text {:style (assoc styles/wizard-text :margin-top 20 :margin-bottom 16)}
      (i18n/label (cond (= :recovery-success step)
                        :t/processing
                        processing? :t/generating-keys
                        :else       :t/this-will-take-few-seconds))])])

(defn top-bar [{:keys [step]}]
  (let [hide-subtitle? (or (= step :enter-phrase))]
    [react/view {:style {:margin-top   16
                         :margin-horizontal 32}}

     [react/text {:style (cond-> styles/wizard-title
                           hide-subtitle?
                           (assoc :margin-bottom 0))}
      (i18n/label
       (cond (= step :enter-phrase)
             :t/multiaccounts-recover-enter-phrase-title
             (= step :recovery-success)
             :t/keycard-recovery-success-header
             :else (keyword (str "intro-wizard-title" (step-kw-to-num step)))))]
     (cond (#{:choose-key :select-key-storage} step)
           ; Use nested text for the "Learn more" link
           [react/nested-text {:style (merge styles/wizard-text
                                             {:height 60})}
            (str (i18n/label (keyword (str "intro-wizard-text" (step-kw-to-num step)))) " ")
            [{:on-press #(re-frame/dispatch [:bottom-sheet/show-sheet :learn-more
                                             {:title (i18n/label (if (= step :choose-key) :t/about-names-title :t/about-key-storage-title))
                                              :content  (i18n/label (if (= step :choose-key) :t/about-names-content :t/about-key-storage-content))}])
              :style {:color colors/blue}
              :accessibility-label :learn-more}
             (i18n/label :learn-more)]]
           (not hide-subtitle?)
           [react/text {:style styles/wizard-text}
            (i18n/label (cond (= step :recovery-success)
                              :t/recovery-success-text
                              :else (keyword (str "intro-wizard-text"
                                                  (step-kw-to-num step)))))]
           :else nil)]))



(defn recovery-success [pubkey name photo-path]
  [react/view {:flex             1
               :justify-content  :space-between
               :background-color colors/white}
   [react/view {:flex            1
                :justify-content :space-between
                :align-items     :center}
    [react/view {:flex-direction  :column
                 :flex            1
                 :justify-content :center
                 :align-items     :center}
     [react/view {:margin-horizontal 16
                  :flex-direction    :column}
      [react/view {:justify-content :center
                   :align-items     :center
                   :margin-bottom   11}
       [react/image {:source {:uri photo-path}
                     :style  {:width         61
                              :height        61
                              :border-radius 30
                              :border-width  1
                              :border-color  colors/black-transparent}}]]
      [react/text {:style           {:text-align  :center
                                     :color       colors/black
                                     :font-weight "500"}
                   :number-of-lines 1
                   :ellipsize-mode  :middle}
       name]
      [quo/text {:style           {:margin-top 4}
                 :monospace       true
                 :color           :secondary
                 :align           :center
                 :number-of-lines 1
                 :ellipsize-mode  :middle}
       (utils/get-shortened-address pubkey)]]]]])

(defview wizard-enter-phrase []
  (letsubs [wizard-state [:intro-wizard/enter-phrase]]
    [react/keyboard-avoiding-view {:style {:flex 1}}
     [topbar/topbar
      {:border-bottom false
       :navigation
       {:on-press #(re-frame/dispatch [:intro-wizard/navigate-back])}}]
     [react/view {:style {:flex            1
                          :justify-content :space-between}}
      [top-bar {:step :enter-phrase}]
      [enter-phrase wizard-state]
      [bottom-bar (merge {:step :enter-phrase
                          :forward-action :multiaccounts.recover/enter-phrase-next-pressed}
                         wizard-state)]]]))

(defview wizard-recovery-success []
  (letsubs [{:keys [pubkey processing? name identicon]} [:intro-wizard/recovery-success]
            existing-account? [:intro-wizard/recover-existing-account?]]
    [react/view {:style {:flex 1}}
     [topbar/topbar
      {:border-bottom false
       :navigation
       {:on-press #(re-frame/dispatch [:intro-wizard/navigate-back])}}]
     [react/view {:style {:flex 1
                          :justify-content :space-between}}
      [top-bar {:step :recovery-success}]
      [recovery-success pubkey name identicon]
      [bottom-bar {:step              :recovery-success
                   :forward-action    :multiaccounts.recover/re-encrypt-pressed
                   :processing?       processing?
                   :existing-account? existing-account?}]]]))
