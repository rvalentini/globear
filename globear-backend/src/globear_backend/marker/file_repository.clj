(ns globear-backend.marker.file-repository
  (:require [clojure.java.io :as io]
            [cheshire.core :refer :all]))

(def path "resources/markers/test.json")

(defn load-all-from-file []
  (println "CALLED")
  (let [file-content (slurp path)
        markers (parse-string file-content)]
    (println markers)
    markers))

(defn save-to-file[marker]
  (let [markers (load-all-from-file)
        updated (update-in markers ["features"] conj marker)]
    (println (str "to be written: " updated))
    (spit path (generate-string updated))))


(defn get-input-stream []
  (io/input-stream "resources/markers/geojson_markers.json"))
