(ns status-im.ui.screens.routing.wallet-stack
  (:require [status-im.ui.screens.currency-settings.views :as currency-settings]
            [status-im.ui.screens.wallet.settings.views :as wallet-settings]
            [status-im.ui.screens.wallet.transactions.views :as wallet-transactions]
            [status-im.ui.screens.wallet.custom-tokens.views :as custom-tokens]
            [status-im.ui.screens.wallet.accounts.views :as wallet.accounts]
            [status-im.ui.screens.wallet.account.views :as wallet.account]
            [status-im.ui.screens.wallet.add-new.views :as add-account]
            [status-im.ui.screens.wallet.account-settings.views :as account-settings]
            [status-im.i18n.i18n :as i18n]))

(def screens
  [{:name      :wallet
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
    :component currency-settings/currency-settings}])