(ns globear.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]
            [globear.picture-overlay :as img]
            [globear.messaging.channel :as channel]
            [cljs.core.async
             :refer [>! <! go chan buffer close! alts! timeout]]
            [cognitect.transit :as transit]))

(def writer (transit/writer :json))



(def globear-map (reagent/atom nil))

(def img-overlay-state
  (reagent/atom {:visible false}))


(defn- expand-picture [src]
  (go (>! channel/request-chan {:action :download :entity :picture :id src}))
  (swap! img-overlay-state assoc :visible true ))


(defn- build-picture-popup [marker]
  (println (aget marker "coordinates" ))
  (-> (new js/mapboxgl.Popup (clj->js {:offset 25}))
      (.setLngLat (clj->js {:lng (aget marker "coordinates" 0) :lat (aget marker "coordinates" 1)}))
      (.setDOMContent
        (hipo/create [:div (for [src (aget marker "pictures")]
                             [:img {:id src
                                    :class "custom-popup-item"
                                    :src "totoro_loading.png"
                                    :on-click #(expand-picture src)}])]))
      (.addTo @globear-map)
      ))


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
;;TODO experiment with cluster layers
;;TODO make lowest zoom level use totoro
;;TODO parse old marker info from within geojson
;;TODO use cooler cluster icons (circle -> totoro holding sign?)x
;;TODO make popup triggered by click on lowest symbol layer
(defn- add-source []
  )

(defn- on-place-click [e]
  (println "Clicked on place")
  (let [marker (aget e "features" 0 "properties")]
    (println marker)
    (println (js->clj marker))
    (println (nth (aget marker "coordinates") 0) )
    (println (type (aget marker "coordinates")))
    (build-picture-popup marker)
    )
  )


(defn add-markers-source-to-map [geojson]
  (println (clj->js geojson))
  (.addSource @globear-map "markers" (clj->js {:type "geojson"
                                               :data "http://localhost:3000/geojson" ;;TODO NOT WORKING WITH INLINE
                                               :cluster true
                                               :clusterMaxZoom 14
                                               :clusterRadius 50}) )

  (.addLayer @globear-map (clj->js {:id "clusters"
                                    :type "circle"
                                    :source "markers"
                                    :filter  ["has" "point_count"]
                                    :paint {:circle-color ["step"
                                                           ["get" "point_count"]
                                                           "#51bbd6"
                                                           100
                                                           "#f1f075"
                                                           750
                                                           "#f28cb1"]
                                            :circle-radius ["step"
                                                            ["get" "point_count"]
                                                            20
                                                            100
                                                            30
                                                            750
                                                            40]}}) )
  (.addLayer @globear-map (clj->js {:id "cluster-count"
                                    :type "symbol"
                                    :source "markers"
                                    :filter ["has" "point_count"]
                                    :layout {:text-field "{point_count_abbreviated}"
                                             :text-font ["DIN Offc Pro Medium" "Arial Unicode MS Bold"]
                                             :text-size 12} }) )
  (.addLayer @globear-map (clj->js {:id "place"
                                    :type "circle"
                                    :source "markers"
                                    :filter ["!" ["has" "point_count"]]
                                    :paint {:circle-color "#FF0000"
                                            :circle-radius 10
                                            :circle-stroke-width 1
                                            :circle-stroke-color "#fff"} }) )
  )





(defn- on-map-load []
  (println "LOADED MAP")
  (go (>! channel/request-chan {:action :download :entity :marker}))  ;;TODO reintroduce logic in new layerd design
  )


(defn- map-init []
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (reset! globear-map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1" :center [103.865158 1.354875], :zoom 10.6})))
  (.addControl @globear-map (new js/mapboxgl.NavigationControl))
  (.on @globear-map "zoomstart" #(on-zoom))
  (.on @globear-map "load" #(on-map-load))
  ;;TODO following lines define new layer behaviour
  (.on @globear-map "click" "place" #(on-place-click %)))




(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @img-overlay-state)) [img/picture-overlay img-overlay-state])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))

