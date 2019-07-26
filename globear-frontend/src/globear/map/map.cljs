(ns globear.map.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]
            [globear.map.picture-overlay :as img]
            [globear.messaging.channel :as channel]
            [cljs.core.async
             :refer [>! <! go chan buffer close! alts! timeout]]
            [clojure.tools.reader.edn :as edn]))




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
      (.addTo @globear-map)
      ))


(defn- on-zoom []
  ;(channel/push-message (str "Oh-my-god-it-zooooomed: " (.getZoom @map)))
  )

;;TODO use cooler cluster icons (circle -> totoro holding sign?)x
(defn- add-source []
  )


;;TODO move to mapbox util
(defn- event->marker [event]
  (let [raw (js->clj (aget event "features" 0 "properties"))]
    {:coordinates (edn/read-string (get-in  raw ["coordinates"]))
     :pictures (edn/read-string (get-in  raw ["pictures"]))
     :id (edn/read-string (get-in  raw ["id"]))
     :comment (get-in raw ["comment"])}))


(defn- on-place-click [e]
  (let [marker (event->marker e)]
    (build-picture-popup marker)))


(defn add-markers-source-to-map [geojson]
  (.addSource @globear-map "markers" (clj->js {:type "geojson"
                                               :data (js->clj (.parse js/JSON geojson) )
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
                                                            40]}
                                    }) )
  (.addLayer @globear-map (clj->js {:id "cluster-count"
                                    :type "symbol"
                                    :source "markers"
                                    :filter ["has" "point_count"]
                                    :layout {:text-field "{point_count_abbreviated}"
                                             :text-font ["DIN Offc Pro Medium" "Arial Unicode MS Bold"]
                                             :text-size 20} }) )
  (.addLayer @globear-map (clj->js {:id "place"
                                    :type "symbol"
                                    :source "markers"
                                    :filter ["!" ["has" "point_count"]]
                                    :layout {
                                             :icon-image "totoro"
                                             :icon-size 0.1
                                             }
                                    }) )
  )





(defn- on-map-load []
  (println "Map loaded!")
  (.loadImage @globear-map "totoro.png" #(.addImage @globear-map "totoro" %2))
  (go (>! channel/request-chan {:action :download :entity :marker})))


(defn- map-init []
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (reset! globear-map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1" :center [103.865158 1.354875], :zoom 10.6})))
  (.addControl @globear-map (new js/mapboxgl.NavigationControl))
  (.on @globear-map "zoomstart" #(on-zoom))
  (.on @globear-map "load" #(on-map-load))
  (.on @globear-map "click" "place" #(on-place-click %)))


(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @img-overlay-state)) [img/picture-overlay img-overlay-state])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))

