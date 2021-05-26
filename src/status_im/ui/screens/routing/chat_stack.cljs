(ns status-im.ui.screens.routing.chat-stack
  (:require [status-im.ui.screens.home.views :as home]
            [status-im.ui.screens.chat.views :as chat]
            [status-im.ui.screens.group.views :as group]
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
            [status-im.ui.screens.stickers.views :as stickers]
            [status-im.ui.screens.notifications-center.views :as notifications-center]
            [status-im.i18n.i18n :as i18n]))

(def components
  [{:name :chat-toolbar
    :component chat/topbar}])

(def screens
  [{:name      :home
    :component home/home}
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
    :component group/contact-toggle-list}
   {:name      :new-group
    :insets    {:top    false
                :bottom true}
    :component group/new-group}
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
    :component membership/membership}])
