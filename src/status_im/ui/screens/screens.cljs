(ns status-im.ui.screens.screens
  (:require [status-im.ui.screens.add-new.new-public-chat.view :as new-public-chat]
            [status-im.ui.screens.wallet.recipient.views :as recipient]
            [status-im.ui.screens.qr-scanner.views :as qr-scanner]
            [status-im.ui.screens.stickers.views :as stickers]
            [status-im.ui.screens.add-new.new-chat.views :as new-chat]
            [status-im.add-new.core :as new-chat.events]
            [status-im.ui.screens.wallet.buy-crypto.views :as wallet.buy-crypto]
            [status-im.ui.screens.group.views :as group-chat]
            [status-im.ui.components.invite.views :as invite]
            [quo.previews.main :as quo.preview]
            [status-im.ui.screens.profile.contact.views :as contact]
            [status-im.ui.screens.notifications-settings.views :as notifications-settings]
            [status-im.ui.screens.wallet.send.views :as wallet]
            [status-im.ui.screens.status.new.views :as status.new]
            [status-im.ui.screens.browser.bookmarks.views :as bookmarks]
            [status-im.ui.screens.communities.invite :as communities.invite]
            [status-im.ui.screens.keycard.onboarding.views :as keycard.onboarding]
            [status-im.ui.screens.keycard.recovery.views :as keycard.recovery]
            [status-im.keycard.core :as keycard.core]
            [status-im.ui.screens.keycard.views :as keycard]
            [status-im.ui.screens.multiaccounts.key-storage.views :as key-storage.views]
            [status-im.ui.screens.home.views :as home]
            [status-im.ui.screens.chat.views :as chat]
            [status-im.ui.screens.referrals.public-chat :as referrals.public-chat]
            [status-im.ui.screens.communities.views :as communities]
            [status-im.ui.screens.communities.community :as community]
            [status-im.ui.screens.communities.create :as communities.create]
            [status-im.ui.screens.communities.import :as communities.import]
            [status-im.ui.screens.communities.profile :as community.profile]
            [status-im.ui.screens.communities.edit :as community.edit]
            [status-im.ui.screens.communities.create-channel :as create-channel]
            [status-im.ui.screens.communities.membership :as membership]
            [status-im.ui.screens.communities.members :as members]
            [status-im.ui.screens.communities.requests-to-join :as requests-to-join]
            [status-im.ui.screens.profile.group-chat.views :as profile.group-chat]
            [status-im.ui.screens.notifications-center.views :as notifications-center]
            [status-im.ui.screens.browser.empty-tab.views :as empty-tab]
            [status-im.ui.screens.browser.views :as browser]
            [status-im.ui.screens.browser.tabs.views :as browser.tabs]
            [status-im.ui.screens.multiaccounts.login.views :as login]
            [status-im.ui.screens.progress.views :as progress]
            [status-im.ui.screens.multiaccounts.views :as multiaccounts]
            [status-im.ui.screens.keycard.authentication-method.views :as keycard.authentication]
            [status-im.ui.screens.onboarding.intro.views :as onboarding.intro]
            [status-im.ui.screens.onboarding.keys.views :as onboarding.keys]
            [status-im.ui.screens.onboarding.password.views :as onboarding.password]
            [status-im.ui.screens.onboarding.storage.views :as onboarding.storage]
            [status-im.ui.screens.onboarding.notifications.views :as onboarding.notifications]
            [status-im.ui.screens.onboarding.welcome.views :as onboarding.welcome]
            [status-im.ui.screens.onboarding.phrase.view :as onboarding.phrase]
            [status-im.ui.screens.currency-settings.views :as currency-settings]
            [status-im.ui.screens.wallet.settings.views :as wallet-settings]
            [status-im.ui.screens.wallet.transactions.views :as wallet-transactions]
            [status-im.ui.screens.wallet.custom-tokens.views :as custom-tokens]
            [status-im.ui.screens.wallet.accounts.views :as wallet.accounts]
            [status-im.ui.screens.wallet.account.views :as wallet.account]
            [status-im.ui.screens.wallet.add-new.views :as add-account]
            [status-im.ui.screens.wallet.account-settings.views :as account-settings]
            [status-im.i18n.i18n :as i18n]
            [status-im.ui.screens.status.views :as status.views]
            [status-im.ui.screens.profile.user.views :as profile.user]
            [status-im.ui.screens.ens.views :as ens]
            [status-im.ui.screens.contacts-list.views :as contacts-list]
            [status-im.ui.screens.bootnodes-settings.edit-bootnode.views
             :as
             edit-bootnode]
            [status-im.ui.screens.bootnodes-settings.views :as bootnodes-settings]
            [status-im.ui.screens.pairing.views :as pairing]
            [status-im.ui.screens.offline-messaging-settings.edit-mailserver.views
             :as
             edit-mailserver]
            [status-im.ui.screens.offline-messaging-settings.views
             :as
             offline-messaging-settings]
            [status-im.ui.screens.dapps-permissions.views :as dapps-permissions]
            [status-im.ui.screens.link-previews-settings.views :as link-previews-settings]
            [status-im.ui.screens.privacy-and-security-settings.views :as privacy-and-security]
            [status-im.ui.screens.privacy-and-security-settings.messages-from-contacts-only :as messages-from-contacts-only]
            [status-im.ui.screens.sync-settings.views :as sync-settings]
            [status-im.ui.screens.advanced-settings.views :as advanced-settings]
            [status-im.ui.screens.help-center.views :as help-center]
            [status-im.ui.screens.glossary.view :as glossary]
            [status-im.ui.screens.about-app.views :as about-app]
            [status-im.ui.screens.mobile-network-settings.view
             :as
             mobile-network-settings]
            [status-im.ui.screens.network.edit-network.views :as edit-network]
            [status-im.ui.screens.network.views :as network]
            [status-im.ui.screens.network.network-details.views :as network-details]
            [status-im.ui.screens.network-info.views :as network-info]
            [status-im.ui.screens.rpc-usage-info :as rpc-usage-info]
            [status-im.ui.screens.log-level-settings.views :as log-level-settings]
            [status-im.ui.screens.fleet-settings.views :as fleet-settings]
            [status-im.ui.screens.profile.seed.views :as profile.seed]
            [status-im.ui.screens.keycard.pin.views :as keycard.pin]
            [status-im.ui.screens.keycard.pairing.views :as keycard.pairing]
            [status-im.ui.screens.keycard.settings.views :as keycard.settings]
            [status-im.ui.screens.appearance.views :as appearance]
            [status-im.ui.screens.privacy-and-security-settings.delete-profile :as delete-profile]
            [status-im.ui.components.colors :as colors]))

(def components
  [{:name :chat-toolbar
    :component chat/topbar}])

(def screens
  [;;INTRO, ONBOARDING, LOGIN

   ;Multiaccounts
   {:name      :multiaccounts
    :insets    {:bottom true}
    :component multiaccounts/multiaccounts}

   ;Login
   {:name      :login
    :insets    {:bottom true}
    :options   {:topBar {:visible false}}
    :component login/login}

   {:name      :progress
    :component progress/progress}

   ;[Onboarding]
   {:name      :intro
    :insets    {:bottom true}
    :component onboarding.intro/intro}

   ;[Onboarding]
   {:name      :get-your-keys
    :insets    {:bottom true}
    :component onboarding.keys/get-your-keys}

   ;[Onboarding]
   {:name      :choose-name
    :options   {:topBar             {:visible false}
                :hardwareBackButton {:popStackOnPress false}}
    :insets    {:bottom true}
    :component onboarding.keys/choose-a-chat-name}

   ;[Onboarding]
   {:name         :select-key-storage
    :back-handler :noop
    :insets       {:bottom true}
    :component    onboarding.storage/select-key-storage}

   ;[Onboarding] Create Password
   {:name         :create-password
    :back-handler :noop
    :insets       {:bottom true}
    :component    onboarding.password/screen}

   ;[Onboarding] Welcome
   {:name         :welcome
    :back-handler :noop
    :insets       {:bottom true}
    :component    onboarding.welcome/welcome}

   ;[Onboarding] Notification
   {:name         :onboarding-notification
    :back-handler :noop
    :insets       {:bottom true}
    :component    onboarding.notifications/notifications-onboarding}

   ;[Onboarding] Recovery
   {:name      :recover-multiaccount-enter-phrase
    :insets    {:bottom true}
    :component onboarding.phrase/enter-phrase}
   {:name         :recover-multiaccount-success
    :back-handler :noop
    :insets       {:bottom true}
    :component    onboarding.phrase/wizard-recovery-success}

   ;; KEYCARD ONBOARDING
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

   ;;CHAT

   ;Home
   {:name      :home
    :component home/home}

   ;Chat
   {:name      :chat
    :options   {:topBar {:title {:component {:name :chat-toolbar :id :chat-toolbar}
                                 :alignment :fill}}}
    :component chat/chat}

   {:name      :group-chat-profile
    :insets    {:top false}
    ;;TODO custom
    :options {:topBar {:visible false}}
    :component profile.group-chat/group-chat-profile}
   {:name      :group-chat-invite
    ;;TODO share
    :options {:topBar {:visible false}}
    :component profile.group-chat/group-chat-invite}

   ;;TODO!!! why two screens ? and also modal!!!
   {:name      :stickers
    :title     (i18n/label :t/sticker-market)
    :component stickers/packs}

   {:name      :stickers-pack
    :component stickers/pack}

   {:name      :notifications-center
    ;;TODO custom nav
    :options {:topBar {:visible false}}
    :component notifications-center/center}
   ;; Community
   {:name      :community
    ;TODO custom
    :options {:topBar {:visible false}}
    :component community/community}
   {:name      :community-management
    :insets    {:top false}
    ;TODO custom
    :options {:topBar {:visible false}}
    :component community.profile/management-container}
   {:name      :community-members
    ;TODO custom subtitle
    :options {:topBar {:visible false}}
    :component members/members-container}
   {:name      :community-requests-to-join
    ;TODO custom subtitle
    :options {:topBar {:visible false}}
    :component requests-to-join/requests-to-join-container}
   {:name      :create-community-channel
    :title (i18n/label :t/create-channel-title)
    :component create-channel/create-channel}
   {:name      :contact-toggle-list
    :insets    {:top    false
                :bottom true}
    :component group-chat/contact-toggle-list}
   {:name      :new-group
    :insets    {:top    false
                :bottom true}
    :component group-chat/new-group}
   {:name      :referral-enclav
    ;;TODO custom
    :options {:topBar {:visible false}}
    :component referrals.public-chat/view}
   {:name      :communities
    :insets    {:bottom true
                :top    false}
    :component communities/communities}
   {:name      :community-import
    :insets    {:bottom true
                :top    false}
    :component communities.import/view}
   {:name      :community-edit
    :insets    {:bottom true
                :top    false}
    :component community.edit/edit}
   {:name      :community-create
    :insets    {:bottom true
                :top    false}
    :component communities.create/view}
   {:name      :community-membership
    :insets    {:bottom true
                :top    false}
    :component membership/membership}

   ;;BROWSER

   {:name      :empty-tab
    :insets    {:top true}
    :options   {:topBar             {:visible false}
                :hardwareBackButton {:popStackOnPress false}}
    :component empty-tab/empty-tab}
   {:name         :browser
    :back-handler :noop
    :options      {:topBar             {:visible false}
                   :hardwareBackButton {:popStackOnPress false}}
    :component    browser/browser}
   {:name      :browser-tabs
    :insets    {:top true}
    :options   {:topBar             {:visible false}
                :hardwareBackButton {:popStackOnPress false}}
    :component browser.tabs/tabs}

   ;;WALLET

   {:name      :wallet
    :insets    {:top false}
    :on-focus  [:wallet/tab-opened]
    :component wallet.accounts/accounts-overview}
   {:name      :wallet-account
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component wallet.account/account}
   {:name      :add-new-account
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component add-account/add-account}
   {:name      :add-new-account-pin
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component add-account/pin}
   {:name      :account-settings
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component account-settings/account-settings}
   {:name      :wallet-transaction-details
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component wallet-transactions/transaction-details}
   {:name      :wallet-settings-assets
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component wallet-settings/manage-assets}
   {:name      :wallet-add-custom-token
    :on-focus  [:wallet/wallet-add-custom-token]
    :title     (i18n/label :t/add-custom-token)
    :component custom-tokens/add-custom-token}
   {:name      :wallet-custom-token-details
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component custom-tokens/custom-token-details}
   {:name      :currency-settings
    :title     (i18n/label :t/main-currency)
    :component currency-settings/currency-settings}

   ;;MY STATUS

   {:name      :status
    :on-focus  [:init-timeline-chat]
    :insets    {:top true}
    :component status.views/timeline}

   ;;PROFILE

   {:name      :my-profile
    :insets    {:top false}
    :component profile.user/my-profile}
   {:name      :contacts-list
    :title     (i18n/label :t/contacts)
    :component contacts-list/contacts-list}
   {:name      :ens-main
    :title (i18n/label :t/ens-usernames)
    :component ens/main}
   {:name      :ens-search
    :title (i18n/label :t/ens-your-username)
    :component ens/search}
   {:name      :ens-checkout
    :title (i18n/label :t/ens-your-username)
    :component ens/checkout}
   {:name      :ens-confirmation
    :title (i18n/label :t/ens-your-username)
    :component ens/confirmation}
   {:name      :ens-terms
    :title (i18n/label :t/ens-terms-registration)
    :component ens/terms}
   {:name      :ens-name-details
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component ens/name-details}
   {:name      :blocked-users-list
    :title (i18n/label :t/blocked-users)
    :component contacts-list/blocked-users-list}
   {:name      :bootnodes-settings
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component bootnodes-settings/bootnodes-settings}
   {:name      :installations
    :title (i18n/label :t/devices)
    :component pairing/installations}
   {:name      :edit-bootnode
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component edit-bootnode/edit-bootnode}
   {:name      :offline-messaging-settings
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component offline-messaging-settings/offline-messaging-settings}
   {:name      :edit-mailserver
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component edit-mailserver/edit-mailserver}
   {:name      :dapps-permissions
    :title (i18n/label :t/dapps-permissions)
    :component dapps-permissions/dapps-permissions}
   {:name      :link-previews-settings
    :title (i18n/label :t/chat-link-previews)
    :component link-previews-settings/link-previews-settings}
   {:name      :privacy-and-security
    :title (i18n/label :t/privacy-and-security)
    :component privacy-and-security/privacy-and-security}
   {:name      :messages-from-contacts-only
    :title (i18n/label :t/accept-new-chats-from)
    :component messages-from-contacts-only/messages-from-contacts-only}
   {:name      :appearance
    :title (i18n/label :t/appearance)
    :component appearance/appearance}
   {:name      :appearance-profile-pic
    :title (i18n/label :t/show-profile-pictures)
    :component appearance/profile-pic}
   {:name      :notifications
    :title (i18n/label :t/notification-settings)
    :component notifications-settings/notifications-settings}
   {:name      :notifications-servers
    :title (i18n/label :t/notification-servers)
    :component notifications-settings/notifications-servers}
   {:name      :sync-settings
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component sync-settings/sync-settings}
   {:name      :advanced-settings
    :title (i18n/label :t/advanced)
    :component advanced-settings/advanced-settings}
   {:name      :help-center
    :title (i18n/label :t/need-help)
    :component help-center/help-center}
   {:name      :glossary
    :title (i18n/label :t/glossary)
    :component glossary/glossary}
   {:name      :about-app
    :title (i18n/label :t/about-app)
    :component about-app/about-app}
   {:name      :manage-dapps-permissions
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component dapps-permissions/manage}
   {:name      :network-settings
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component network/network-settings}
   {:name      :network-details
    :title (i18n/label :t/network-details)
    :component network-details/network-details}
   {:name      :network-info
    :title (i18n/label :t/network-info)
    :component network-info/network-info}
   {:name      :rpc-usage-info
    :title (i18n/label :t/rpc-usage-info)
    :component rpc-usage-info/usage-info}
   {:name      :edit-network
    :title (i18n/label :t/add-network)
    :component edit-network/edit-network}
   {:name      :log-level-settings
    :title (i18n/label :t/log-level-settings)
    :component log-level-settings/log-level-settings}
   {:name      :fleet-settings
    :title (i18n/label :t/fleet-settings)
    :component fleet-settings/fleet-settings}
   {:name      :mobile-network-settings
    :title (i18n/label :t/mobile-network-settings)
    :component mobile-network-settings/mobile-network-settings}
   {:name      :backup-seed
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component profile.seed/backup-seed}
   {:name       :delete-profile
    :transition :presentation-ios
    :insets     {:bottom true}
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component  delete-profile/delete-profile}
   ;; {:name:my-profile-ext-settings
   ;;          :component}

   ;; KEYCARD
   {:name      :keycard-settings
    :title (i18n/label :t/status-keycard)
    :component keycard.settings/keycard-settings}
   {:name      :reset-card
    :title (i18n/label :t/reset-card)
    :component keycard.settings/reset-card}
   {:name      :keycard-pin
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component keycard.settings/reset-pin}
   {:name      :enter-pin-settings
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component keycard.pin/enter-pin}
   {:name      :change-pairing-code
    ;;TODO dynamic title
    :options   {:topBar {:visible false}}
    :component keycard.pairing/change-pairing-code}

   ;;MODALS

   ;[Chat] New Chat
   {:name       :new-chat
    :on-focus   [::new-chat.events/new-chat-focus]
    ;;TODO custom topbar
    :options    {:topBar {:visible false}}
    :component  new-chat/new-chat}

   ;[Chat] New Public chat
   {:name       :new-public-chat
    :insets     {:bottom true}
    :title      (i18n/label :t/new-public-group-chat)
    :component  new-public-chat/new-public-chat}

   ;[Chat] Link preview settings
   {:name       :link-preview-settings
    :title      (i18n/label :t/chat-link-previews)
    :component  link-previews-settings/link-previews-settings}

   ;[Chat] Edit nickname
   {:name       :nickname
    :insets     {:bottom true}
    ;;TODO subtitle
    :options    {:topBar {:visible false}}
    :component  contact/nickname}

   ;[Group chat] Edit group chat name
   {:name       :edit-group-chat-name
    :insets     {:bottom true}
    :title      (i18n/label :t/edit-group)
    :component  group-chat/edit-group-chat-name}

   ;[Group chat] Add participants
   {:name       :add-participants-toggle-list
    :on-focus   [:group/add-participants-toggle-list]
    :insets     {:bottom true}
    ;;TODO subtitle
    :options    {:topBar {:visible false}}
    :component  group-chat/add-participants-toggle-list}

   #_{:name       :create-group-chat
      :transition :presentation-ios
      ;;TODO stack
      :component  chat-stack/new-group-chat}
   #_{:name       :communities
      :transition :presentation-ios
      ;;TODO stack
      :component  chat-stack/communities}

   ;[Communities] Invite people
   {:name      :invite-people-community
    :component communities.invite/invite
    :insets    {:bottom true}}

   ;New Contact
   {:name       :new-contact
    :on-focus   [::new-chat.events/new-chat-focus]
    ;;TODO custom topbar
    :options    {:topBar {:visible false}}
    :component  new-chat/new-contact}

   ;Refferal invite
   {:name       :referral-invite
    :insets     {:bottom true}
    :title      (i18n/label :t/invite-friends)
    :component  invite/referral-invite}

   ;[Wallet] Recipient
   {:name       :recipient
    :insets     {:bottom true}
    ;;TODO custom topbar
    :options    {:topBar {:visible false}}
    :component  recipient/recipient}

   ;[Wallet] New favourite
   {:name       :new-favourite
    :insets     {:bottom true}
    :title      (i18n/label :t/new-favourite)
    :component  recipient/new-favourite}

   ;QR Scanner
   {:name      :qr-scanner
    :insets    {:top false :bottom false}
    ;;TODO custom topbar
    :options   {:topBar    {:visible false}
                :navigationBar {:backgroundColor colors/black-persist}
                :statusBar {:backgroundColor colors/black-persist
                            :style           :light}}
    :component qr-scanner/qr-scanner}

   ;;TODO WHY MODAL?
   ;[Profile] Notifications settings
   {:name         :notifications-settings
    :back-handler :noop
    :insets       {:bottom true}
    :title        (i18n/label :t/notification-settings)
    :component    notifications-settings/notifications-settings}

   ;;TODO WHY MODAL?
   ;[Profile] Notifications Advanced settings
   {:name         :notifications-advanced-settings
    :back-handler :noop
    :title        (i18n/label :t/notification-settings)
    :insets       {:bottom true}
    :component    notifications-settings/notifications-advanced-settings}

   ;[Wallet] Prepare Transaction
   {:name       :prepare-send-transaction
    :insets     {:bottom true}
    ;;TODO custom back handler
    :options    {:topBar {:visible false}}
    :component  wallet/prepare-send-transaction}

   ;[Wallet] Request Transaction
   {:name       :request-transaction
    :insets     {:bottom true}
    ;;TODO custom back handler
    :options    {:topBar {:visible false}}
    :component  wallet/request-transaction}

   ;[Wallet] Buy crypto
   {:name       :buy-crypto
    :insets     {:bottom true}
    :component  wallet.buy-crypto/container}

   ;[Wallet] Buy crypto website
   {:name       :buy-crypto-website
    :insets     {:bottom true}
    ;;TODO subtitle
    :options    {:topBar {:visible false}}
    :component  wallet.buy-crypto/website}

   ;My Status
   {:name       :my-status
    :insets     {:bottom true}
    :title      (i18n/label :t/my-status)
    :component  status.new/my-status}

   ;[Browser] New bookmark
   {:name       :new-bookmark
    :insets     {:bottom true}
    ;;TODO dynamic title
    :options    {:topBar {:visible false}}
    :component  bookmarks/new-bookmark}

   ;Profile
   {:name       :profile
    :insets     {:bottom true}
    ;;TODO custom toolbar
    :options    {:topBar {:visible false}}
    :component  contact/profile}

   ;KEYCARD
   {:name         :keycard-onboarding-intro
    :back-handler keycard.core/onboarding-intro-back-handler
    ;;TODO dynamic
    :options      {:topBar {:visible false}}
    :component    keycard.onboarding/intro}
   {:name         :keycard-onboarding-puk-code
    :back-handler :noop
    ;;TODO dynamic
    :options      {:topBar {:visible false}}
    :component    keycard.onboarding/puk-code}
   {:name         :keycard-onboarding-pin
    :back-handler :noop
    ;;TODO dynamic
    :options      {:topBar {:visible false}}
    :component    keycard.onboarding/pin}
   {:name         :keycard-recovery-pair
    :back-handler :noop
    :title        (i18n/label :t/step-i-of-n {:number 2 :step 1})
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
    :component keycard/not-keycard}

   ;;KEYSTORAGE
   {:name      :actions-not-logged-in
    :component key-storage.views/actions-not-logged-in}
   {:name      :actions-logged-in
    :component key-storage.views/actions-logged-in}
   {:name      :storage
    :component key-storage.views/storage}

   (when js/goog.DEBUG
     ;;TODO DEV stack
     {:name      :quo-preview
      :insets    {:top false :bottom false}
      :component quo.preview/preview-stack})])