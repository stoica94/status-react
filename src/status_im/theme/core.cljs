(ns status-im.theme.core
  (:require [status-im.ui.components.colors :as colors]
            [quo.theme :as quo-theme]
            ;;TODO remove dep
            ["react-native-navigation-bar-color" :default changeNavigationBarColor]))

(defn change-theme [theme]
  (quo-theme/set-theme theme)
  (colors/set-theme theme))
  ;;TODO update screens
  ;;(status-bar/set-status-bar nil)
  ;(changeNavigationBarColor colors/white (= theme :light)))
