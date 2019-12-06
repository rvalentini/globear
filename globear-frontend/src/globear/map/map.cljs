(ns globear.map.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]
            [globear.map.picture-overlay :as img]
            [globear.map.mapbox-util :as util]
            [globear.messaging.channel :as channel]
            [globear.map.mapbox-config :as conf]
            [globear.map.context-menu :as menu]
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


;TODO move to image-overlay
(defn- expand-picture [src]
  (go (>! channel/request-chan {:action :download :entity :picture :id src}))
  (swap! img-overlay-state assoc :visible true ))


(defn- build-picture-popup [marker]
  ;;request each picture from the backend-server
  (doseq [picture (:pictures marker)]
    (go (>! channel/request-chan {:action :download :entity :thumbnail :id picture})))
  (-> (new js/mapboxgl.Popup (clj->js {:offset 25}))
      (.setLngLat (clj->js {:lng (nth  (:coordinates marker) 0) :lat (nth (:coordinates marker) 1)}))
      (.setDOMContent
        (hipo/create [:div (for [src (:pictures marker)]
                             [:img {:id src
                                    :class "custom-popup-item"
                                    :src "totoro_loading.png"
                                    :on-click #(expand-picture src)}])]))
      (.addTo @globear-map)))


(defn- on-place-click [e]
  (let [marker (util/event->marker e)]
    (build-picture-popup marker)))


;;TODO find pixel positions of all markers
;;TODO move to map_utils
(defn get-current-marker-positions []

  (let [source (.getSource @globear-map "markers")]
    (.log js/console source)
    )


  )







;TODO make sure not to add marker on top of existing markers
(defn- on-right-click [e]
  (get-current-marker-positions)
  (.log js/console e)
  (let [features (.queryRenderedFeatures @globear-map (clj->js {:layers ["place"]}) )]
    (.log js/console features)
    ;;TODO iterate over features (each "lowest-level" Totoro marker) and match event
    ;;TODO lat,lng with each features lat,lng
    )

  ;;TODO close popup here if opened currently
  (let [coordinates [(aget e "lngLat" "lng") (aget e "lngLat" "lat")]
        position [(aget e "point" "x") (aget e "point" "y")]]
    (menu/open-context-menu coordinates position context-menu-state)))


(defn add-markers-source-to-map [geojson]
  (reset! marker-source geojson)
  (util/init-map-with-source globear-map geojson))


(defn- on-map-load []
  (println "Map loaded!")
  ;;TODO Totoro not loaded fast enough? On inital map-load -> only number appearing
  (.loadImage @globear-map "totoro.png" #(.addImage @globear-map "totoro" %2))
  (.loadImage @globear-map "totoro_cluster.png" #(.addImage @globear-map "totoro-cluster" %2))
  (go (>! channel/request-chan {:action :download :entity :marker})))

(defn- register-listeners []
  (.on @globear-map "load" #(on-map-load))
  (.on @globear-map "click" "place" #(on-place-click %))
  (.on @globear-map "contextmenu" #(on-right-click %))
  (.on @globear-map "click" #(menu/close-context-menu context-menu-state))
  (.on @globear-map "click" "clusters" #(util/zoom-on-clicked-cluster globear-map %1)))

(defn- map-init []
  (set! (.-accessToken js/mapboxgl) conf/token)
  (reset! globear-map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/ck16hxsq50d6n1clhleanmfhl" :center [-3.688495 40.399189], :zoom 10.6})))
  (.addControl @globear-map (new js/mapboxgl.NavigationControl))
  (register-listeners))


(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @img-overlay-state)) [img/picture-overlay
                                              img-overlay-state])
   (if (true? (:visible @context-menu-state)) [menu/context-menu
                                               context-menu-state
                                               util/add-new-marker-to-source-layer
                                               globear-map
                                               marker-source])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))

