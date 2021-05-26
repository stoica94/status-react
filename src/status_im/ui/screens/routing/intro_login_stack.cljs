(ns status-im.ui.screens.routing.intro-login-stack
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
            [status-im.ui.screens.onboarding.intro.views :as onboarding.intro]
            [status-im.ui.screens.onboarding.keys.views :as onboarding.keys]
            [status-im.ui.screens.onboarding.password.views :as onboarding.password]
            [status-im.ui.screens.onboarding.storage.views :as onboarding.storage]
            [status-im.ui.screens.onboarding.notifications.views :as onboarding.notifications]
            [status-im.ui.screens.onboarding.welcome.views :as onboarding.welcome]
            [status-im.ui.screens.onboarding.phrase.view :as onboarding.phrase]
            [status-im.i18n.i18n :as i18n]))

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
   {:name      :recover-multiaccount-enter-phrase
    :component onboarding.phrase/enter-phrase}
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