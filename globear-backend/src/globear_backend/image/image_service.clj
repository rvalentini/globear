(ns globear-backend.image.image-service
  (:require [clojure.java.io :refer [file]]
            [mikera.image.core :as img]
            [clojure.java.io :as io]))



(defn picture-exists? [id]
  (.exists (io/as-file (str "resources/pictures/" id ".jpg"))))


(defn load-image-from-file [id]
  (let [path (str "resources/pictures/" id ".jpg")]
    (println "Loading image" path "from file")
    (img/load-image (file path))))


(defn load-image-as-stream [id]
  (let [path (str "resources/pictures/" id ".jpg")]
    (io/input-stream path)))


(defn save-image-to-file [])