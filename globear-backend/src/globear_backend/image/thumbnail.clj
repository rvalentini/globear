(ns globear-backend.image.thumbnail
  (:require [mikera.image.core :as img]
            [clojure.java.io :refer [file]]))


(def height 150)

(defn- load-image-from-file [id]
  (let [path (str "resources/pictures/" id ".jpg")]
    (println "Loading image" path "from file")
    (img/load-image (file path))))


(defn- save-image-to-file [image id]
  (let [path (str "resources/thumbnails/" id "_thumbnail.png")]
    (println "Saving thumbnail" path "to file")
    (img/save image path :quality 1.0 :progressive false)))


(defn- resize-image
  "Resized the given image to fixed a fixed height (defined above) and recalculates the height
  to keep the image"
  [src-image]
  (println "Generating thumbnail...")
  (let [calculated-dimension (/ (* (long height) (img/width src-image)) (img/height src-image))]
    (img/resize src-image calculated-dimension height)))


(defn generate-thumbnail [id]
  (println (str "Generating thumbnail for " id))
  (-> id
      (load-image-from-file)
      (resize-image)
      (save-image-to-file id)))


