(ns globear.map.mapbox-util
  (:require [clojure.tools.reader.edn :as edn]))


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
                                    :layout {:icon-image "totoro"
                                             :icon-size 0.1}})))

(defn add-item-count-layer-to-map [globear-map]
  (.addLayer @globear-map (clj->js {:id "cluster-count"
                                    :type "symbol"
                                    :source "markers"
                                    :filter ["has" "point_count"]
                                    :layout {:text-field "{point_count_abbreviated}"
                                             :text-font ["DIN Offc Pro Medium" "Arial Unicode MS Bold"]
                                             :text-size 20} }) )


  )

(defn add-cluster-layer-to-map [globear-map]
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
                                                            40]}})))

(defn add-source-layer-to-map [globear-map geojson]
  (.addSource @globear-map "markers" (clj->js {:type "geojson"
                                               :data (js->clj (.parse js/JSON geojson) )
                                               :cluster true
                                               :clusterMaxZoom 14
                                               :clusterRadius 50})))