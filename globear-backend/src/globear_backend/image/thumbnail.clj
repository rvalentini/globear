(ns globear-backend.image.thumbnail
  (:require [mikera.image.core :as img]
            [clojure.java.io :refer [file]])
  (:import (java.awt.image BufferedImage)
           (java.awt Color)))


(def height 100)

(defn- remove-alpha [image]
  (let [copy (BufferedImage. (img/width image) (img/height image) (BufferedImage/TYPE_INT_RGB))]
    (let [graphics (.createGraphics copy)]
      (.setColor graphics (Color/WHITE))
      (.fillRect graphics 0 0 (.getWidth copy) (.getHeight copy))
      (.drawImage graphics 0 0 nil)
      (.dispose graphics)
      copy)))


(defn- load-image-from-file [id]
  (let [path (str "resources/pictures/" id ".jpg")]
    (println "Loading image" path "from file")
    (img/load-image (file path))))


(defn- save-image-to-file [image id]
  (let [path (str "resources/thumbnails/" id "_thumbnail.jpg")]
    (println "Saving thumbnail" path "to file")
    (img/save image path)))


(defn- resize-image
  "Resized the given image to fixed a fixed height (defined above) and recalculates the heigh
  to keep the image"
  [src-image]
  (println "Generating thumbnail...")
  (let [calculated-dimension (/ (* (long height) (img/width src-image)) (img/height src-image))]
    (img/resize src-image calculated-dimension height)))


(defn generate-thumbnail [id]
  (-> id
      (load-image-from-file)
      (resize-image)
      (remove-alpha)
      (save-image-to-file id)))


