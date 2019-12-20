(ns globear-backend.image.thumbnail-service
  (:require [mikera.image.core :as img]
            [clojure.java.io :as io])
  (:import (java.io IOException)))


(defn thumbnail-exists? [id]
  (.exists (io/as-file (str "resources/thumbnails/" id "_thumbnail.png"))))


(defn save-thumbnail-to-file [image id]
  (let [path (str "resources/thumbnails/" id "_thumbnail.png")]
    (println "Saving thumbnail" path "to file")
    (img/save image path :quality 1.0 :progressive false)))


(defn load-thumbnail-as-stream [id]
  (let [path (str "resources/thumbnails/" id "_thumbnail.png")]
    (try
      (io/input-stream path)
      (catch IOException ex
        (println (str "ERROR: Could not load thumbnail from file: " ex))))))






