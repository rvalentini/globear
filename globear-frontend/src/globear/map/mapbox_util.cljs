(ns globear.map.mapbox-util
  (:require [clojure.tools.reader.edn :as edn]
            [globear.messaging.channel :as channel]
            [cljs.core.async
             :refer [>! <! go chan buffer close! alts! timeout]]))


(defn event->marker [event]
  (let [raw (js->clj (aget event "features" 0 "properties"))]
    {:coordinates (edn/read-string (get-in  raw ["coordinates"]))
     :pictures (edn/read-string (get-in  raw ["pictures"]))
     :id (edn/read-string (get-in  raw ["id"]))
     :comment (get-in raw ["comment"])}))


(defn add-place-symbol-layer-to-map [globear-map]
  (.addLayer @globear-map (clj->js {:id "place"
                                    :type "symbol"
                                    :source "markers"
                                    :filter ["!" ["has" "point_count"]]
                                    :layout {:icon-image "bear_scaled"
                                             :icon-size 0.15}})))


(defn add-cluster-layer-to-map [globear-map]
  (.addLayer @globear-map (clj->js {:id "clusters"
                                    :type "symbol"
                                    :source "markers"
                                    :filter  [">" ["get" "point_count"] 0]
                                    :layout {:icon-image "bear_cluster"
                                             :icon-size 0.11
                                             :icon-allow-overlap true}})))


(defn add-item-count-layer-to-map [globear-map]
  (.addLayer @globear-map (clj->js {:id "cluster-count"
                                    :type "symbol"
                                    :source "markers"
                                    :filter ["has" "point_count"]
                                    :layout {:text-field "{point_count_abbreviated}"
                                             :text-font ["DIN Offc Pro Medium" "Arial Unicode MS Bold"]
                                             :text-size 20
                                             :text-offset [0.65 -1.25] } }) ))


(defn add-source-layer-to-map [globear-map geojson]
  (.addSource @globear-map "markers" (clj->js {:type "geojson"
                                               :data (js->clj (.parse js/JSON geojson) )
                                               :cluster true
                                               :clusterMaxZoom 14
                                               :clusterRadius 50})))

(defn init-map-with-source [globear-map source]
  (do
    (add-source-layer-to-map globear-map source)
    (add-cluster-layer-to-map globear-map)
    (add-item-count-layer-to-map globear-map)
    (add-place-symbol-layer-to-map globear-map)))


(defn- build-marker [coordinates comment id]
  {:type "Feature"
   :properties {:id (str id)
                :coordinates coordinates
                :pictures []
                :comment comment}
   :geometry {:type "Point"
              :coordinates [(first coordinates) (second coordinates) 0]}})

(defn- append-marker-to-source [coordinates comment marker-source]
  (let [asJson (.parse js/JSON @marker-source)
        asEdn (js->clj asJson :keywordize-keys true)
        features (:features asEdn)
        id (inc (count features))
        new-marker (build-marker coordinates comment id)]
    (go (>! channel/request-chan {:action :upload :entity :marker :marker new-marker}))
    (assoc-in asEdn [:features] (conj features new-marker))))


(defn add-new-marker-to-source-layer [globear-map coordinates comment marker-source]
  (let [source-layer (.getSource @globear-map "markers")
        source (append-marker-to-source coordinates comment marker-source)]
    (reset! marker-source (.stringify js/JSON (clj->js source)) )
    (.setData source-layer (clj->js source))))


(defn extract-distances-to-click [globear-map markers click]
  (let [projection (.project @globear-map (aget click "lngLat"))
        [px, py] [(aget projection "x") (aget projection "y")]]
    (->> markers
         (map #(aget % "properties" "coordinates"))
         (map #(edn/read-string %))
         (map #(.project @globear-map (clj->js %)))
         (map #(identity [(aget % "x") (aget % "y")]))
         (map #(Math/sqrt
                 (+
                   (Math/pow (- (first %) px) 2)
                   (Math/pow (- (second %) py) 2)))))))


(defn is-click-on-marker [globear-map click]
  (let [markers (.queryRenderedFeatures @globear-map (clj->js {:layers ["place"]}) )
        distances (extract-distances-to-click globear-map markers click)]
    (some #(<= % 35) distances)))


(defn get-closest-marker-id [globear-map click]
  (let [markers (.queryRenderedFeatures @globear-map (clj->js {:layers ["place"]}) )
        ids (->> markers
                 (map #(aget % "properties" "id")))
        distances (extract-distances-to-click globear-map markers click)
        ids-with-distances (map vector ids distances)]

        (first (first (sort-by #(second %) ids-with-distances)))))


(defn zoom-on-clicked-cluster [globear-map event]
  (let [features (.queryRenderedFeatures @globear-map (aget event "point") {:layers ["clusters"]})
        cluster-id (aget features 0 "properties" "cluster_id")
        source (.getSource @globear-map "markers")
        center (aget features 0 "geometry" "coordinates")]
    (.getClusterExpansionZoom source cluster-id #(if (nil? %1)
                                                   (.easeTo @globear-map (clj->js {:center (js->clj center)
                                                                                  :zoom %2}))
                                                   (println "ERROR on cluster-zoom event!")))))

