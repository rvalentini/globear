(ns globear.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]
            [globear.picture-overlay :as img]
            [globear.messaging.channel :as channel]
            [cljs.core.async
             :refer [>! <! go chan buffer close! alts! timeout]]))

(def globear-map (reagent/atom nil))

(def img-overlay-state
  (reagent/atom {:visible false}))


(defn- expand-picture [src]
  ;TODO load resource via action
  (go (>! channel/request-chan {:action :download :entity :picture :id src}))
  (swap! img-overlay-state assoc :visible true ))


;TODO fix image orientation in some cases
(defn- build-picture-popup [marker]
  (-> (new js/mapboxgl.Popup (clj->js {:offset 25}))
      (.setDOMContent
        (hipo/create [:div (for [src (aget marker "properties" "pictures")]
                             [:img {:id src
                                    :class "custom-popup-item"
                                    :src "totoro_loading.png"
                                    :on-click #(expand-picture src)}])]))))


(defn- build-mapbox-marker [marker-data]
  (-> {:type "Feature" :geometry {:type "Point" } :properties {:title "a" :description "b"}}
      (assoc-in [:geometry :coordinates] (:coordinates marker-data))
      (assoc-in [:properties :pictures] (:pictures marker-data))))


(defn add-marker-to-map [marker]
  ;;request each picture from the backend-server
  (doseq [picture (:pictures marker)]
    (go (>! channel/request-chan {:action :download :entity :thumbnail :id picture})))
  (let [mapbox-marker (build-mapbox-marker marker)]
    (let [element (.createElement js/document "div") marker-js (clj->js mapbox-marker)]
      (set! (.-className element) "marker")
      (-> (new js/mapboxgl.Marker element)
          (.setLngLat (aget marker-js "geometry" "coordinates"))
          (.setPopup (build-picture-popup marker-js))
          (.addTo @globear-map)))))


(defn- on-zoom []
  ;(channel/push-message (str "Oh-my-god-it-zooooomed: " (.getZoom @map)))
  )


(defn- map-init []
  (go (>! channel/request-chan {:action :download :entity :marker}))
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (reset! globear-map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1" :center [103.865158 1.354875], :zoom 10.6})))
  (.addControl @globear-map (new js/mapboxgl.NavigationControl))
  (.on @globear-map "zoomstart" #(on-zoom)))



(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @img-overlay-state)) [img/picture-overlay img-overlay-state])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))

