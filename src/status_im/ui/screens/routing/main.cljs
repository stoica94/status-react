(ns status-im.ui.screens.routing.main
  (:require-macros [status-im.utils.views :as views])
  (:require [status-im.ui.screens.add-new.new-public-chat.view :as new-public-chat]
            [status-im.ui.screens.wallet.recipient.views :as recipient]
            [status-im.ui.screens.qr-scanner.views :as qr-scanner]
            [status-im.ui.screens.stickers.views :as stickers]
            [status-im.ui.screens.add-new.new-chat.views :as new-chat]
            [status-im.add-new.core :as new-chat.events]
            [status-im.ui.screens.routing.chat-stack :as chat-stack]
            [status-im.ui.screens.wallet.buy-crypto.views :as wallet.buy-crypto]
            [status-im.ui.screens.group.views :as group-chat]
            [status-im.ui.components.invite.views :as invite]
            [quo.previews.main :as quo.preview]
            [status-im.ui.screens.profile.contact.views :as contact]
            [status-im.ui.screens.notifications-settings.views :as notifications-settings]
            [status-im.ui.screens.wallet.send.views :as wallet]
            [status-im.ui.screens.link-previews-settings.views :as link-previews]
            [status-im.ui.screens.status.new.views :as status.new]
            [status-im.ui.screens.browser.bookmarks.views :as bookmarks]
            [status-im.ui.screens.communities.invite :as communities.invite]
            [status-im.ui.screens.keycard.onboarding.views :as keycard.onboarding]
            [status-im.ui.screens.keycard.recovery.views :as keycard.recovery]
            [status-im.keycard.core :as keycard.core]
            [status-im.ui.screens.keycard.views :as keycard]
            [status-im.ui.screens.multiaccounts.key-storage.views :as key-storage.views]
            [status-im.i18n.i18n :as i18n]))

(def screens
  [{:name      :stickers-pack-modal
    :component stickers/pack}
   {:name         :welcome
    :back-handler :noop}
   ;:component    home/welcome}
   {:name       :new-chat
    :on-focus   [::new-chat.events/new-chat-focus]
    :transition :presentation-ios
    ;;TODO custom topbar
    :options   {:topBar {:visible false}}
    :component  new-chat/new-chat}
   {:name       :new-contact
    :on-focus   [::new-chat.events/new-chat-focus]
    :transition :presentation-ios
    ;;TODO custom topbar
    :options   {:topBar {:visible false}}
    :component  new-chat/new-contact}
   {:name       :link-preview-settings
    :transition :presentation-ios
    :title      (i18n/label :t/chat-link-previews)
    :component  link-previews/link-previews-settings}
   {:name       :new-public-chat
    :transition :presentation-ios
    :insets     {:bottom true}
    :title  (i18n/label :t/new-public-group-chat)
    :component  new-public-chat/new-public-chat}
   {:name       :nickname
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO subtitle
    :options   {:topBar {:visible false}}
    :component  contact/nickname}
   {:name       :edit-group-chat-name
    :transition :presentation-ios
    :insets     {:bottom true}
    :title  (i18n/label :t/edit-group)
    :component  group-chat/edit-group-chat-name}
   #_{:name       :create-group-chat
      :transition :presentation-ios
      ;;TODO stack
      :component  chat-stack/new-group-chat}
   #_{:name       :communities
      :transition :presentation-ios
      ;;TODO stack
      :component  chat-stack/communities}
   {:name       :referral-invite
    :transition :presentation-ios
    :insets     {:bottom true}
    :title        (i18n/label :t/invite-friends)
    :component  invite/referral-invite}
   {:name       :add-participants-toggle-list
    :on-focus   [:group/add-participants-toggle-list]
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO subtitle
    :options   {:topBar {:visible false}}
    :component  group-chat/add-participants-toggle-list}
   {:name       :recipient
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO custom topbar
    :options   {:topBar {:visible false}}
    :component  recipient/recipient}
   {:name       :new-favourite
    :transition :presentation-ios
    :insets     {:bottom true}
    :title  (i18n/label :t/new-favourite)
    :component  recipient/new-favourite}
   {:name      :qr-scanner
    :insets    {:top false :bottom false}
    ;;TODO custom topbar
    :options   {:topBar {:visible false}}
    :component qr-scanner/qr-scanner}
   {:name         :notifications-settings
    :back-handler :noop
    :insets       {:bottom true}
    :title (i18n/label :t/notification-settings)
    :component    notifications-settings/notifications-settings}
   {:name         :notifications-advanced-settings
    :back-handler :noop
    :title (i18n/label :t/notification-settings)
    :insets       {:bottom true}
    :component    notifications-settings/notifications-advanced-settings}
   {:name       :prepare-send-transaction
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO custom back handler
    :options   {:topBar {:visible false}}
    :component  wallet/prepare-send-transaction}
   {:name       :request-transaction
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO custom back handler
    :options   {:topBar {:visible false}}
    :component  wallet/request-transaction}
   {:name       :my-status
    :transition :presentation-ios
    :insets     {:bottom true}
    :title         (i18n/label :t/my-status)
    :component  status.new/my-status}
   {:name       :new-bookmark
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component  bookmarks/new-bookmark}
   {:name       :profile
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO custom toolbar
    :options   {:topBar {:visible false}}
    :component  contact/profile}
   {:name       :buy-crypto
    :transition :presentation-ios
    :insets     {:bottom true}
    :component wallet.buy-crypto/container}
   {:name       :buy-crypto-website
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO subtitle
    :options   {:topBar {:visible false}}
    :component  wallet.buy-crypto/website}
   {:name      :invite-people-community
    :component communities.invite/invite
    :insets     {:bottom true}}

   ;;TODO DEV stack
   {:name      :quo-preview
    :insets    {:top false :bottom false}
    :component quo.preview/preview-stack}

   {:name         :keycard-onboarding-intro
    :back-handler keycard.core/onboarding-intro-back-handler
    ;;TODO dynamic
    :options   {:topBar {:visible false}}
    :component    keycard.onboarding/intro}
   {:name         :keycard-onboarding-puk-code
    :back-handler :noop
    ;;TODO dynamic
    :options   {:topBar {:visible false}}
    :component    keycard.onboarding/puk-code}
   {:name         :keycard-onboarding-pin
    :back-handler :noop
    ;;TODO dynamic
    :options   {:topBar {:visible false}}
    :component    keycard.onboarding/pin}
   {:name         :keycard-recovery-pair
    :back-handler :noop
    :title (i18n/label :t/step-i-of-n {:number 2
                                       :step   1})
    :component    keycard.recovery/pair}
   {:name      :seed-phrase
    ;;TODO subtitle
    :options   {:topBar {:visible false}}
    :component key-storage.views/seed-phrase}
   {:name      :keycard-recovery-pin
    ;;TODO dynamic
    :options   {:topBar {:visible false}}
    :component keycard.recovery/pin}
   {:name      :keycard-wrong
    ;;TODO move to popover?
    :options   {:topBar {:visible false}}
    :component keycard/wrong}
   {:name      :not-keycard
    :options   {:topBar {:visible false}}
    ;;TODO move to popover?
    :component keycard/not-keycard}])
