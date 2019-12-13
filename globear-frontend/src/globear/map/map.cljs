(ns globear.map.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]
            [globear.map.picture-overlay :as img]
            [globear.map.mapbox-util :as util]
            [globear.messaging.channel :as channel]
            [globear.map.mapbox-config :as conf]
            [globear.map.context-menu :as context-menu]
            [globear.map.marker-menu :as marker-menu]
            [cljs.core.async
             :refer [>! <! go chan buffer close! alts! timeout]]))


(def globear-map (reagent/atom nil))

(def marker-source (reagent/atom nil))

(def img-overlay-state
  (reagent/atom {:visible false}))

(def context-menu-state
  (reagent/atom {:visible false
                 :position {:x 0 :y 0}
                 :coordinates {:lat 0 :lng 0}}))

(def marker-menu-state
  (reagent/atom {:visible false
                 :position {:x 0 :y 0}
                 :coordinates {:lat 0 :lng 0}}))

(def picture-popup-state
  (reagent/atom {:popup nil}))


;TODO move to image-overlay
(defn- expand-picture [src]
  (go (>! channel/request-chan {:action :download :entity :picture :id src}))
  (swap! img-overlay-state assoc :visible true ))


(defn- build-picture-popup [marker]
  ;;request each picture from the backend-server
  (doseq [picture (:pictures marker)]
    (go (>! channel/request-chan {:action :download :entity :thumbnail :id picture})))

  (let [popup (new js/mapboxgl.Popup (clj->js {:offset 25}))]
    (-> popup
        (.setLngLat (clj->js {:lng (nth  (:coordinates marker) 0) :lat (nth (:coordinates marker) 1)}))
        (.setDOMContent
          (hipo/create [:div (for [src (:pictures marker)]
                               [:img {:id src
                                      :class "custom-popup-item"
                                      :src "totoro_loading.png"
                                      :on-click #(expand-picture src)}])]))
        (.addTo @globear-map))
        (swap! picture-popup-state assoc :popup popup )))


(defn- on-place-click [e]
  (let [marker (util/event->marker e)]
    (build-picture-popup marker)))


(defn- on-right-click [e]
  (let [marker-clicked (util/is-click-on-marker globear-map e)
        closest-marker (util/get-closest-marker-id globear-map e)
        coordinates [(aget e "lngLat" "lng") (aget e "lngLat" "lat")]
        position [(aget e "point" "x") (aget e "point" "y")]]

    (marker-menu/close marker-menu-state)
    (context-menu/close context-menu-state)

    (if (:popup @picture-popup-state) (.remove (:popup @picture-popup-state)))

    (if marker-clicked
      (marker-menu/open coordinates position marker-menu-state)
      (context-menu/open coordinates position context-menu-state))))


(defn add-markers-source-to-map [geojson]
  (reset! marker-source geojson)
  (util/init-map-with-source globear-map geojson))


(defn- on-map-load []
  (println "Map loaded!")
  ;;TODO Totoro not loaded fast enough? On inital map-load -> only number appearing
  (.loadImage @globear-map "bear_scaled.png" #(.addImage @globear-map "bear_scaled" %2))
  (.loadImage @globear-map "totoro_cluster.png" #(.addImage @globear-map "totoro-cluster" %2))
  (go (>! channel/request-chan {:action :download :entity :marker})))

(defn- register-listeners []
  (.on @globear-map "load" #(on-map-load))
  (.on @globear-map "click" "place" #(on-place-click %))
  (.on @globear-map "contextmenu" #(on-right-click %))
  (.on @globear-map "click" #(do (context-menu/close context-menu-state)
                                 (marker-menu/close marker-menu-state)))
  (.on @globear-map "click" "clusters" #(util/zoom-on-clicked-cluster globear-map %1)))

(defn- map-init []
  (set! (.-accessToken js/mapboxgl) conf/token)
  (reset! globear-map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/ck16hxsq50d6n1clhleanmfhl" :center [-3.688495 40.399189], :zoom 13})))
  (.addControl @globear-map (new js/mapboxgl.NavigationControl))
  (register-listeners))


(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @img-overlay-state)) [img/picture-overlay
                                              img-overlay-state])
   (if (true? (:visible @context-menu-state)) [context-menu/context-menu
                                               context-menu-state
                                               util/add-new-marker-to-source-layer
                                               globear-map
                                               marker-source])
   (if (true? (:visible @marker-menu-state)) [marker-menu/marker-menu
                                               marker-menu-state
                                               globear-map
                                               marker-source])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))

