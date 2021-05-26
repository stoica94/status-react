(ns status-im.ui.screens.onboarding.phrase.view
  (:require [status-im.ui.components.react :as react]
            [status-im.ui.components.colors :as colors]
            [status-im.i18n.i18n :as i18n]
            [re-frame.core :as re-frame]
            [status-im.utils.security :as security]
            [quo.core :as quo]
            [status-im.utils.datetime :as datetime]))

(defn enter-phrase [_]
  (let [show-bip39-password? (reagent.core/atom false)
        pressed-in-at (atom nil)]
    (fn [{:keys [processing?
                 passphrase-word-count
                 next-button-disabled?
                 passphrase-error]}]
      [react/keyboard-avoiding-view {:flex             1
                                     :background-color colors/white}
       [quo/text {:weight :bold
                  :align  :center
                  :size   :x-large}
        (i18n/label :t/multiaccounts-recover-enter-phrase-title)]
       [react/pressable
        {:style        {:background-color   colors/white
                        :flex               1
                        :justify-content    :center
                        :padding-horizontal 16}
         ;; BIP39 password input will be shown only after pressing on screen
         ;; for longer than 2 seconds
         :on-press-in  (fn []
                         (reset! pressed-in-at (datetime/now)))
         :on-press-out (fn []
                         (when (>= (datetime/seconds-ago @pressed-in-at) 2)
                           (reset! show-bip39-password? true)))}
        [react/view
         [quo/text-input
          {:on-change-text      #(re-frame/dispatch [:multiaccounts.recover/enter-phrase-input-changed
                                                     (security/mask-data %)])
           :auto-focus          true
           :error               (when passphrase-error (i18n/label passphrase-error))
           :accessibility-label :passphrase-input
           :placeholder         (i18n/label :t/seed-phrase-placeholder)
           :show-cancel         false
           :bottom-value        40
           :multiline           true
           :auto-correct        false
           :monospace           true}]
         [react/view {:align-items :flex-end}
          [react/view {:flex-direction   :row
                       :align-items      :center
                       :padding-vertical 8
                       :opacity          (if passphrase-word-count 1 0)}
           [quo/text {:color (if next-button-disabled? :secondary :main)
                      :size  :small}
            (when-not next-button-disabled?
              "✓ ")
            (i18n/label-pluralize passphrase-word-count :t/words-n)]]]
         (when @show-bip39-password?
           ;; BIP39 password (`passphrase` in BIP39
           ;; https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki#from-mnemonic-to-seed)
           ;; is an advanced security feature which allows to add an arbitrary
           ;; extra word to your existing mnemonic. The password is an empty
           ;; string if not provided. If the password is added a completely
           ;; different key will be created.
           [quo/text-input
            {:on-change-text
             #(re-frame/dispatch [:multiaccounts.recover/enter-passphrase-input-changed
                                  (security/mask-data %)])
             :placeholder (i18n/label :t/bip39-password-placeholder)
             :show-cancel false}])]]
       [react/view {:align-items :center}
        [react/text {:style {:color         colors/gray
                             :font-size     14
                             :margin-bottom 8
                             :text-align    :center}}
         (i18n/label :t/multiaccounts-recover-enter-phrase-text)]
        (when processing?
          [react/view {:flex 1 :align-items :center}
           [react/activity-indicator {:size      :large
                                      :animating true}]
           [react/text {:style {:color      colors/gray
                                :margin-top 8}}
            (i18n/label :t/processing)]])]])))