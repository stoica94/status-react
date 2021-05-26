(ns status-im.ui.screens.routing.status-stack
  (:require [status-im.ui.screens.status.views :as status.views]))

(def screens
  [{:name      :status
    :on-focus  [:init-timeline-chat]
    :insets    {:top true}
    :component status.views/timeline}])