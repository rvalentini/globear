(ns globear.map.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]
            [globear.map.picture-overlay :as img]
            [globear.map.mapbox-util :as util]
            [globear.messaging.channel :as channel]
            [globear.map.mapbox-config :as conf]
            [cljs.core.async
             :refer [>! <! go chan buffer close! alts! timeout]]))


(def globear-map (reagent/atom nil))

(def img-overlay-state
  (reagent/atom {:visible false}))


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


(defn add-markers-source-to-map [geojson]
  (util/add-source-layer-to-map globear-map geojson)
  ((juxt util/add-cluster-layer-to-map-totoro
         util/add-item-count-layer-to-map
         util/add-place-symbol-layer-to-map)
     globear-map))


(defn- on-map-load []
  (println "Map loaded!")
  (.loadImage @globear-map "totoro.png" #(.addImage @globear-map "totoro" %2))
  (.loadImage @globear-map "totoro_cluster.png" #(.addImage @globear-map "totoro-cluster" %2))
  (go (>! channel/request-chan {:action :download :entity :marker})))

(defn- register-listeners []
  ;(.on @globear-map "zoomstart" #(on-zoom))
  (.on @globear-map "load" #(on-map-load))
  (.on @globear-map "click" "place" #(on-place-click %))
  (.on @globear-map "click" "clusters" #(util/zoom-on-clicked-cluster @globear-map %1))
  )

(defn- map-init []
  (set! (.-accessToken js/mapboxgl) conf/token)
  (reset! globear-map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1" :center [-3.688495 40.399189], :zoom 8.6})))
  (.addControl @globear-map (new js/mapboxgl.NavigationControl))
  (register-listeners))


(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @img-overlay-state)) [img/picture-overlay img-overlay-state])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))

