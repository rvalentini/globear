(ns globear.map.mapbox-util
  (:require [clojure.tools.reader.edn :as edn]
            [cljs-bean.core :refer [bean ->clj ->js]]))


(defn event->marker [event]
  ;;TODO understand what is going on here
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

(defn add-cluster-layer-to-map-totoro [globear-map]
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
                                             :text-offset [0.6 -1.1] } }) )


  )

;(defn add-cluster-layer-to-map [globear-map]
;  (.addLayer @globear-map (clj->js {:id "clusters"
;                                    :type "circle"
;                                    :source "markers"
;                                    :filter  ["has" "point_count"]
;                                    :paint {:circle-color ["step"
;                                                           ["get" "point_count"]
;                                                           "#51bbd6"
;                                                           100
;                                                           "#f1f075"
;                                                           750
;                                                           "#f28cb1"]
;                                            :circle-radius ["step"
;                                                            ["get" "point_count"]
;                                                            20
;                                                            100
;                                                            30
;                                                            750
;                                                            40]}})))

(defn add-source-layer-to-map [globear-map geojson]
  (.addSource @globear-map "markers" (clj->js {:type "geojson"
                                               :data (js->clj (.parse js/JSON geojson) )
                                               :cluster true
                                               :clusterMaxZoom 14
                                               :clusterRadius 50})))

(defn obj->clj [globear-map obj]
  ;(println (.log js/console obj))
    (let [features (.queryRenderedFeatures globear-map (aget obj "point") {:layers ["clusters"]})]
      (println (.log js/console features))
      (println "-------")
      ;(println (js->clj (aget features 0 "properties" "cluster_id")))
      (let [feature (js->clj (aget features 0 "properties") :keywordize-keys true)]
        ;TODO insights: only from "properties layer" downwards working
        ;TODO one layer above there is a map-box specific part which cannot be parsed by neither js->clj nor ->clj
        ;TODO try to make this somehow generic
        (println feature)
        (println (:cluster_id feature))

        )
      ;(println (edn/read-string (js->clj (aget features [0 "properties"]))))
      ;(println (edn/read-string (js->clj features)))
      ;(println (:y parsed))

      )

    ;(let [converted (edn/read-string (js->clj obj)) ]
    ;
    ;  (println (get-in converted [:point 0 :properties :cluster_id]))
    ;
    ;  )


  )


(defn zoom-on-clicked-cluster [globear-map event]
  ;(println (.log js/console event))
  (println (.log js/console (.queryRenderedFeatures globear-map (aget event "point") {:layers ["clusters"]})))

  (let [features (.queryRenderedFeatures globear-map (aget event "point") {:layers ["clusters"]})]
    (println (.log js/console (aget features [0 "properties" "cluster_id"])))

    )

  (let [features (.queryRenderedFeatures globear-map (aget event "point") {:layers ["clusters"]})
        cluster-id (get-in features [0 "properties" "cluster_id"])
        source (.getSource globear-map "markers")
        center (get-in features ["geometry" "coordinates"])]

    (.getClusterExpansionZoom source cluster-id #(if (nil? %1)
                                                   (.easeTo map {:center center
                                                                 :zoom %2})
                                                   (println "ERROR on cluster-zoom event!")))

    )

  )