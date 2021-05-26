(ns status-im.ui.screens.routing.profile-stack
  (:require [status-im.ui.screens.profile.user.views :as profile.user]
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
            [status-im.ui.components.tabbar.styles :as tabbar.styles]
            [status-im.ui.screens.appearance.views :as appearance]
            [status-im.ui.screens.notifications-settings.views :as notifications-settings]
            [status-im.ui.screens.privacy-and-security-settings.delete-profile :as delete-profile]
            [status-im.i18n.i18n :as i18n]))

(def screens
  [{:name      :my-profile
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
    :component keycard.pairing/change-pairing-code}])
