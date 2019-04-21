(ns globear.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]
            [globear.picture-overlay :as img]
            [globear.test-pics :as test-resource]
            [globear.messaging.channel :as channel]))


(def map (reagent/atom nil))

(def img-overlay-state
  (reagent/atom {:visible false
                 :src nil}))



(defn- expand-picture [src]
  (swap! img-overlay-state assoc :src src)
  (swap! img-overlay-state assoc :visible true ))


(defn- build-picture-popup [marker]
  (-> (new js/mapboxgl.Popup (clj->js {:offset 25}))
      (.setDOMContent
        (hipo/create [:div (for [src (aget marker "properties" "pictures")]
                             [:img {:class "custom-popup-item" :src src :on-click #(expand-picture src)}])]))))


(defn- build-mapbox-marker [marker-data]
  (-> {:type "Feature" :geometry {:type "Point" } :properties {:title "a" :description "b"}}
      (assoc-in [:geometry :coordinates] (:coordinates marker-data)) ;;TODO use from marker
      (assoc-in [:properties :pictures] (:pictures marker-data)))) ;;TODO use from marker


(defn add-marker-to-map [marker]
  (let [mapbox-marker (build-mapbox-marker marker)]
    (let [element (.createElement js/document "div") marker-js (clj->js mapbox-marker)]
      (set! (.-className element) "marker")
      (-> (new js/mapboxgl.Marker element)
          (.setLngLat (aget marker-js "geometry" "coordinates"))
          (.setPopup (build-picture-popup marker-js))
          (.addTo @map)))))


(defn- on-zoom []
  (channel/push-message (str "Oh-my-god-it-zooooomed: " (.getZoom @map))))


(defn- map-init []
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (reset! map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1" :center [103.865158 1.354875], :zoom 10.6})))
  (.addControl @map (new js/mapboxgl.NavigationControl))
  (add-marker-to-map {:coordinates [103.865158 1.354875] :pictures test-resource/pics })  ;;TODO iterate over real markers
  (add-marker-to-map {:coordinates [103.722079 1.324356] :pictures test-resource/pics })  ;;TODO iterate over real markers
  (.on @map "zoomstart" #(on-zoom)))



(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @img-overlay-state)) [img/picture-overlay img-overlay-state])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))

