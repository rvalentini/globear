(ns globear.map.popup
  (:require [globear.map.picture-overlay :as img]
            [cljs.core.async :refer [go, >!]]
            [hipo.core :as hipo]
            [globear.messaging.channel :as chan]
            [globear.map.map-state :as state]))


(defn- open-marker-popup [globear-map marker]
  ;;request each thumbnail for this marker popup from backend-server
  (doseq [picture (:pictures marker)]
    (go (>! chan/request-chan {:action :download :entity :thumbnail :id picture})))

  (let [popup (new js/mapboxgl.Popup (clj->js {:offset 30}))]
    (-> popup
        (.setLngLat (clj->js {:lng (nth  (:coordinates marker) 0) :lat (nth (:coordinates marker) 1)}))
        (.setDOMContent
          (hipo/create [:div (for [src (:pictures marker)]
                               [:img {:id       src
                                      :class    "custom-popup-item"
                                      :src      "bear.png"
                                      :on-click #(img/expand-picture src)}])]))
        (.addTo @globear-map))
    (swap! state/picture-popup-state assoc :popup popup)))
