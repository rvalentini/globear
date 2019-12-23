(ns globear.map.picture-overlay
  (:require [globear.messaging.channel :as channel]
            [globear.map.map-state :as state]
            [cljs.core.async
             :refer [>! <! go chan buffer close! alts! timeout]]))


(defn close-picture-overlay [state]
  (swap! state assoc :visible false))


(defn picture-overlay [state]
  [:div
   [:div {:class "img-fullscreen-overlay" :on-click #(close-picture-overlay state)}]
   [:img {:class  "img-fullscreen"
          :id "img-overlay"
          :src "bear.png"
          :on-click #(close-picture-overlay state) }]])


(defn expand-picture [src]
  (go (>! channel/request-chan {:action :download :entity :picture :id src}))
  (swap! state/img-overlay-state assoc :visible true))
