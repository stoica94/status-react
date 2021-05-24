(ns status-im.ui.screens.onboarding.storage.views
  (:require [re-frame.core :as re-frame]
            [status-im.i18n.i18n :as i18n]
            [status-im.ui.components.react :as react]
            [status-im.utils.debounce :refer [dispatch-and-chill]]
            [quo.core :as quo]
            [status-im.ui.screens.onboarding.views :as ui])
  (:require-macros [status-im.utils.views :refer [defview letsubs]]))

(defn storage-entry [{:keys [type icon title desc]} selected-storage-type]
  [:<>
   [quo/list-header (i18n/label type)]
   [quo/list-item {:accessibility-label (keyword (str "select-storage-" type))
                   :on-press            #(re-frame/dispatch [:intro-wizard/on-key-storage-selected type])
                   :icon                icon
                   :accessory           :radio
                   :active              (= type selected-storage-type)
                   :title               (i18n/label title)
                   :subtitle            (i18n/label desc)
                   :subtitle-max-lines  2}]])

(defview select-key-storage []
  (letsubs [{:keys [selected-storage-type]} [:intro-wizard/select-key-storage]]
    [:<>
     #_[topbar/topbar
        {:navigation
         (if (:recovering? wizard-state)
           {:label    (i18n/label :t/cancel)
            :on-press #(re-frame/dispatch [:intro-wizard/navigate-back])}
           {:on-press #(re-frame/dispatch [:intro-wizard/navigate-back])})}]
     [react/view {:style {:flex 1}}
      [ui/title-with-description :t/intro-wizard-title3 :t/intro-wizard-text3]
      [ui/learn-more :t/about-key-storage-title :t/about-key-storage-content]
      [react/view {:style {:margin-top 60}}
       [storage-entry {:type  :default
                       :icon  :main-icons/mobile
                       :title :t/this-device
                       :desc  :t/this-device-desc}
        selected-storage-type]
       [react/view {:style {:height 16}}]
       [storage-entry {:type  :advanced
                       :icon  :main-icons/keycard
                       :title :t/keycard
                       :desc  :t/keycard-desc}
        selected-storage-type]]]
     [ui/next-button #(dispatch-and-chill [:rnn-navigate-to :create-password] 300)]]))