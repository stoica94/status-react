(ns status-im.ui.screens.routing.intro-login-stack
  (:require-macros [status-im.utils.views :as views])
  (:require [status-im.ui.screens.multiaccounts.login.views :as login]
            [status-im.ui.screens.progress.views :as progress]
            [status-im.ui.screens.multiaccounts.views :as multiaccounts]
            [status-im.keycard.core :as keycard.core]
            [status-im.ui.screens.routing.key-storage-stack :as key-storage-stack]
            [status-im.ui.screens.keycard.onboarding.views :as keycard.onboarding]
            [status-im.ui.screens.keycard.recovery.views :as keycard.recovery]
            [status-im.ui.screens.keycard.views :as keycard]
            [status-im.ui.screens.keycard.authentication-method.views
             :as
             keycard.authentication]
            [status-im.ui.screens.routing.core :as navigation]
            [status-im.ui.screens.onboarding.intro.views :as onboarding.intro]
            [status-im.ui.screens.onboarding.keys.views  :as onboarding.keys]
            [status-im.ui.screens.onboarding.password.views  :as onboarding.password]
            [status-im.ui.screens.onboarding.storage.views  :as onboarding.storage]
            [status-im.ui.screens.onboarding.notifications.views  :as onboarding.notifications]
            [status-im.ui.screens.onboarding.welcome.views  :as onboarding.welcome]))

(defonce stack (navigation/create-stack))

;; NOTE(Ferossgp): There is a point at init when we do not know if there are
;; multiaccounts or no, and we show intro - we could show progress at this time
;; for a better UX
(views/defview intro-stack []
  (views/letsubs [multiaccounts [:multiaccounts/multiaccounts]
                  loading [:multiaccounts/loading]]
    [stack {:header-mode :none}
     [(cond
        loading
        {:name      :progress-start
         :component progress/progress}

        (empty? multiaccounts)
        {} #_{:name      :intro
              :component intro/intro}

        :else
        {:name      :multiaccounts
         :component multiaccounts/multiaccounts})
      {:name      :progress
       :component progress/progress}
      {:name      :login
       :component login/login}
      #_{:name      :create-multiaccount-generate-key
         :component intro/wizard-generate-key}
      #_{:name         :create-multiaccount-choose-key
         :back-handler :noop
         :component    intro/wizard-choose-key}
      #_{:name         :create-multiaccount-select-key-storage
         :back-handler :noop
         :component    intro/wizard-select-key-storage}
      #_{:name         :create-multiaccount-create-code
         :back-handler :noop
         :component    password/screen}
      #_{:name      :recover-multiaccount-enter-phrase
         :component intro/wizard-enter-phrase}
      #_{:name         :recover-multiaccount-select-storage
         :back-handler :noop
         :component    intro/wizard-select-key-storage}
      #_{:name         :recover-multiaccount-enter-password
         :back-handler :noop
         :component    password/screen}
      #_ {:name         :recover-multiaccount-success
          :back-handler :noop
          :component    intro/wizard-recovery-success}
      {:name         :keycard-onboarding-intro
       :back-handler keycard.core/onboarding-intro-back-handler
       :component    keycard.onboarding/intro}
      {:name         :keycard-onboarding-puk-code
       :back-handler :noop
       :component    keycard.onboarding/puk-code}
      {:name         :keycard-onboarding-pin
       :back-handler :noop
       :component    keycard.onboarding/pin}
      {:name         :keycard-onboarding-recovery-phrase
       :back-handler :noop
       :component    keycard.onboarding/recovery-phrase}
      {:name         :keycard-onboarding-recovery-phrase-confirm-word1
       :back-handler :noop
       :component    keycard.onboarding/recovery-phrase-confirm-word}
      {:name         :keycard-onboarding-recovery-phrase-confirm-word2
       :back-handler :noop
       :component    keycard.onboarding/recovery-phrase-confirm-word}
      {:name         :keycard-recovery-intro
       :back-handler :noop
       :component    keycard.recovery/intro}
      {:name         :keycard-recovery-pair
       :back-handler :noop
       :component    keycard.recovery/pair}
      {:name         :keycard-recovery-success
       :back-handler :noop
       :insets       {:bottom true}
       :component    keycard.recovery/success}
      {:name      :keycard-recovery-no-key
       :component keycard.recovery/no-key}
      {:name      :keycard-recovery-pin
       :component keycard.recovery/pin}
      {:name      :keycard-authentication-method
       :component keycard.authentication/keycard-authentication-method}
      {:name      :keycard-login-pin
       :component keycard/login-pin}
      {:name      :keycard-blank
       :component keycard/blank}
      {:name      :keycard-wrong
       :component keycard/wrong}
      {:name      :keycard-unpaired
       :component keycard/unpaired}
      {:name      :not-keycard
       :component keycard/not-keycard}
      {:name      :key-storage-stack
       :insets    {:top false}
       :component key-storage-stack/key-storage-stack}]]))

(def screens
  [{:name      :progress-start
    :component progress/progress}
   {:name      :intro
    :insets       {:bottom true}
    :component onboarding.intro/intro}
   {:name      :multiaccounts
    :insets       {:bottom true}
    :component multiaccounts/multiaccounts}
   {:name      :progress
    :component progress/progress}
   {:name      :login
    :insets       {:bottom true}
    :component login/login}
   {:name      :get-your-keys
    :insets       {:bottom true}
    :component onboarding.keys/get-your-keys}
   {:name         :choose-name
    :back-handler :noop
    :options      {:topBar {:visible false}
                   :hardwareBackButton {:popStackOnPress false}}
    :insets       {:bottom true}
    :component    onboarding.keys/choose-a-chat-name}
   {:name         :select-key-storage
    :back-handler :noop
    :insets       {:bottom true}
    :component    onboarding.storage/select-key-storage}
   {:name         :create-password
    :back-handler :noop
    :insets       {:bottom true}
    :component    onboarding.password/screen}
   {:name         :welcome
    :back-handler :noop
    :insets       {:bottom true}
    :component    onboarding.welcome/welcome}
   {:name :notifications-onboarding
    :back-handler :noop
    :insets       {:bottom true}
    :component     onboarding.notifications/notifications-onboarding}
   #_{:name      :recover-multiaccount-enter-phrase
      :component intro/wizard-enter-phrase}
   #_{:name         :recover-multiaccount-select-storage
      :back-handler :noop
      :component    intro/wizard-select-key-storage}
   #_{:name         :recover-multiaccount-enter-password
      :back-handler :noop
      :component    password/screen}
   #_{:name         :recover-multiaccount-success
      :back-handler :noop
      :component    intro/wizard-recovery-success}
   {:name         :keycard-onboarding-intro
    :back-handler keycard.core/onboarding-intro-back-handler
    :component    keycard.onboarding/intro}
   {:name         :keycard-onboarding-puk-code
    :back-handler :noop
    :component    keycard.onboarding/puk-code}
   {:name         :keycard-onboarding-pin
    :back-handler :noop
    :component    keycard.onboarding/pin}
   {:name         :keycard-onboarding-recovery-phrase
    :back-handler :noop
    :component    keycard.onboarding/recovery-phrase}
   {:name         :keycard-onboarding-recovery-phrase-confirm-word1
    :back-handler :noop
    :component    keycard.onboarding/recovery-phrase-confirm-word}
   {:name         :keycard-onboarding-recovery-phrase-confirm-word2
    :back-handler :noop
    :component    keycard.onboarding/recovery-phrase-confirm-word}
   {:name         :keycard-recovery-intro
    :back-handler :noop
    :component    keycard.recovery/intro}
   {:name         :keycard-recovery-pair
    :back-handler :noop
    :component    keycard.recovery/pair}
   {:name         :keycard-recovery-success
    :back-handler :noop
    :insets       {:bottom true}
    :component    keycard.recovery/success}
   {:name      :keycard-recovery-no-key
    :component keycard.recovery/no-key}
   {:name      :keycard-recovery-pin
    :component keycard.recovery/pin}
   {:name      :keycard-authentication-method
    :component keycard.authentication/keycard-authentication-method}
   {:name      :keycard-login-pin
    :component keycard/login-pin}
   {:name      :keycard-blank
    :component keycard/blank}
   {:name      :keycard-wrong
    :component keycard/wrong}
   {:name      :keycard-unpaired
    :component keycard/unpaired}
   {:name      :not-keycard
    :component keycard/not-keycard}
   {:name      :key-storage-stack
    :insets    {:top false}
    :component key-storage-stack/key-storage-stack}])