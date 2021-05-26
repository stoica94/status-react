(ns status-im.ui.screens.routing.browser-stack
  (:require [status-im.ui.screens.browser.empty-tab.views :as empty-tab]
            [status-im.ui.screens.browser.views :as browser]
            [status-im.ui.screens.browser.tabs.views :as browser.tabs]))

(def screens
  [{:name      :empty-tab
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
    :component browser.tabs/tabs}])
