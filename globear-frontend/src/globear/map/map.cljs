(ns globear.map.map
  (:require [reagent.core :as reagent]
            [globear.map.picture-overlay :as img]
            [globear.map.mapbox-util :as util]
            [globear.messaging.channel :as channel]
            [globear.map.mapbox-config :as conf]
            [globear.map.context-menu :as context-menu]
            [globear.map.marker-menu :as marker-menu]
            [globear.map.map-state :as state]
            [globear.map.popup :as popup]
            [cljs.core.async
             :refer [>! <! go chan buffer close! alts! timeout]]))


(def globear-map (reagent/atom nil))


(defn- on-place-click [e]
  (let [marker (util/event->marker e)]
    (popup/open-marker-popup globear-map marker)))


(defn- on-right-click [e]
  (let [marker-clicked (util/is-click-on-marker globear-map e)
        closest-marker (util/get-closest-marker-id globear-map e)
        coordinates [(aget e "lngLat" "lng") (aget e "lngLat" "lat")]
        position [(aget e "point" "x") (aget e "point" "y")]]

    (marker-menu/close state/marker-menu-state)
    (context-menu/close state/context-menu-state)

    (if (:popup @state/picture-popup-state) (.remove (:popup @state/picture-popup-state)))

    (if marker-clicked
      (marker-menu/open coordinates position state/marker-menu-state)
      (context-menu/open coordinates position state/context-menu-state))))


(defn add-markers-source-to-map [geojson]
  (reset! state/marker-source geojson)
  (util/init-map-with-source globear-map geojson))


(defn- on-map-load []
  (println "Map loaded!")
  (.loadImage @globear-map "bear_scaled.png" #(.addImage @globear-map "bear_scaled" %2))
  (.loadImage @globear-map "bear_cluster_scaled.png" #(.addImage @globear-map "bear_cluster" %2))
  (go (>! channel/request-chan {:action :download :entity :marker})))

(defn- register-listeners []
  (.on @globear-map "load" #(on-map-load))
  (.on @globear-map "click" "place" #(on-place-click %))
  (.on @globear-map "contextmenu" #(on-right-click %))
  (.on @globear-map "click" #(do (context-menu/close state/context-menu-state)
                                 (marker-menu/close state/marker-menu-state)))
  (.on @globear-map "click" "clusters" #(util/zoom-on-clicked-cluster globear-map %1)))

(defn- map-init []
  (set! (.-accessToken js/mapboxgl) conf/token)
  (reset! globear-map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/ck16hxsq50d6n1clhleanmfhl" :center [-3.688495 40.399189], :zoom 13})))
  (.addControl @globear-map (new js/mapboxgl.NavigationControl))
  (register-listeners))


(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @state/img-overlay-state)) [img/picture-overlay
                                                    state/img-overlay-state])
   (if (true? (:visible @state/context-menu-state)) [context-menu/context-menu
                                                     state/context-menu-state
                                                     util/add-new-marker-to-source-layer
                                                     globear-map
                                                     state/marker-source])
   (if (true? (:visible @state/marker-menu-state)) [marker-menu/marker-menu
                                                    state/marker-menu-state
                                                    globear-map
                                                    state/marker-source])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))

