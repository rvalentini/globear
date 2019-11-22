(ns globear-backend.marker.file-repository
  (:require [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [clojure.edn :as edn]))

(def path "resources/markers/test.json")  ;;TODO configure the real deal

(defn load-all-from-file []
  (let [file-content (slurp path)
        markers (parse-string file-content)]
    markers))

(defn save-to-file[marker]
  (let [markers (load-all-from-file)
        updated (update-in markers ["features"] conj (edn/read-string marker))]
    (println "Saving updated markers to file!")
    (spit path (generate-string updated))))

;;TODO use this
(defn get-input-stream []
  (io/input-stream "resources/markers/geojson_markers.json"))
