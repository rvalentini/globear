(ns globear-backend.marker.file-repository
  (:require [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [clojure.edn :as edn])
  (:import (java.io  IOException)))

(def path "resources/markers/geojson_markers.json")


(defn load-all-markers-from-file []
  (let [file-content (slurp path)
        markers (parse-string file-content)]
    markers))


(defn save-marker-to-file[marker]
  (try
    (let [markers (load-all-markers-from-file)
          updated (update-in markers ["features"] conj (edn/read-string marker))]
      (println "Saving updated markers to file!")
      (spit path (generate-string updated)))
    (catch IOException ex
      (println (str "ERROR: Could not save markers to file: " ex))
      ex)))


(defn load-markers-as-stream []
  (try
    (io/input-stream "resources/markers/geojson_markers.json")
    (catch IOException ex
      (println (str "ERROR: Could not load markers from file: " ex)))))
