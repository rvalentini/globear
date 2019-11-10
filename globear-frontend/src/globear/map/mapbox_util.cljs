(ns globear.map.mapbox-util
  (:require [clojure.tools.reader.edn :as edn]))


(defn event->marker [event]
  ;;TODO find better mor generic solution for the js->cljs transpiling problem
  ;;TODO insight: there is some map-box specific element on the same hierarchy level as "properties",
  ;;TODO probably something within _vectorTileFeature, which causes the js->cljs transpiling to fail
  ;;TODO maybe it's an encoding problem?
  (let [raw (js->clj (aget event "features" 0 "properties"))]
    {:coordinates (edn/read-string (get-in  raw ["coordinates"]))
     :pictures (edn/read-string (get-in  raw ["pictures"]))
     :id (edn/read-string (get-in  raw ["id"]))
     :comment (get-in raw ["comment"])}))


(defn init-map-with-source [globear-map source]
  (do
    (add-source-layer-to-map globear-map source)
    (add-cluster-layer-to-map globear-map)
    (add-item-count-layer-to-map globear-map)
    (add-place-symbol-layer-to-map globear-map)))


(defn add-place-symbol-layer-to-map [globear-map]
  (.addLayer @globear-map (clj->js {:id "place"
                                    :type "symbol"
                                    :source "markers"
                                    :filter ["!" ["has" "point_count"]]
                                    :layout {:icon-image "totoro"
                                             :icon-size 0.1}})))


(defn add-cluster-layer-to-map [globear-map]
  (.addLayer @globear-map (clj->js {:id "clusters"
                                    :type "symbol"
                                    :source "markers"
                                    :filter  [">" ["get" "point_count"] 0]
                                    :layout {:icon-image "totoro-cluster"
                                             :icon-size 0.1
                                             :icon-allow-overlap true}})))


(defn add-item-count-layer-to-map [globear-map]
  (.addLayer @globear-map (clj->js {:id "cluster-count"
                                    :type "symbol"
                                    :source "markers"
                                    :filter ["has" "point_count"]
                                    :layout {:text-field "{point_count_abbreviated}"
                                             :text-font ["DIN Offc Pro Medium" "Arial Unicode MS Bold"]
                                             :text-size 20
                                             :text-offset [0.6 -1.1] } }) ))


(defn add-source-layer-to-map [globear-map geojson]
  (.addSource @globear-map "markers" (clj->js {:type "geojson"
                                               :data (js->clj (.parse js/JSON geojson) )
                                               :cluster true
                                               :clusterMaxZoom 14
                                               :clusterRadius 50})))


(defn- build-marker [coordinates comment id]
  {:type "Feature"
   :properties {:id (str id)
                :coordinates coordinates
                :pictures []
                :comment comment}
   :geometry {:type "Point"
              :coordinates [(first coordinates) (second coordinates) 0]}})

;;TODO send the diff (= new marker part) to the server
(defn- append-marker-to-source [coordinates comment marker-source]
  (let [asJson (.parse js/JSON @marker-source)
        asEdn (js->clj asJson :keywordize-keys true)
        features (:features asEdn)
        id (inc (count features))]
    (assoc-in asEdn [:features] (conj features (build-marker coordinates comment id)))))


(defn add-new-marker-to-source-layer [globear-map coordinates comment marker-source]
  (let [source-layer (.getSource @globear-map "markers")
        source (append-marker-to-source coordinates comment marker-source)]
    (reset! marker-source (.stringify js/JSON (clj->js source)) )
    (.setData source-layer (clj->js source))))


(defn zoom-on-clicked-cluster [globear-map event]
  (let [features (.queryRenderedFeatures @globear-map (aget event "point") {:layers ["clusters"]})
        cluster-id (aget features 0 "properties" "cluster_id")
        source (.getSource @globear-map "markers")
        center (aget features 0 "geometry" "coordinates")]
    (.getClusterExpansionZoom source cluster-id #(if (nil? %1)
                                                   (.easeTo @globear-map (clj->js {:center (js->clj center)
                                                                                  :zoom %2}))
                                                   (println "ERROR on cluster-zoom event!")))))

