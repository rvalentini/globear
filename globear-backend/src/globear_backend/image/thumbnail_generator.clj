(ns globear-backend.image.thumbnail-generator
  (:require [clojure.string :as string]
            [globear-backend.image.image-service :refer [load-image-from-file] ]
            [globear-backend.image.thumbnail-service :refer [save-thumbnail-to-file, thumbnail-exists?] ]
            [mikera.image.core :as img]))


(def height 150)

(defn- walk-path []
  (->> (str "resources/pictures")
       clojure.java.io/file
       file-seq
       (map #(.getName %))
       (filter #(string/includes? % ".jpg"))
       (map #(subs % 0 (- (count %) 4)))))


(defn- resize-image
  "Resized the given image to fixed a fixed height (defined above) and recalculates the height
  to keep the image"
  [src-image]
  (println "Generating thumbnail...")
  (let [calculated-dimension (/ (* (long height) (img/width src-image)) (img/height src-image))]
    (img/resize src-image calculated-dimension height)))


(defn- generate-thumbnail [id]
  (println (str "Generating thumbnail for " id))
  (-> id
      (load-image-from-file)
      (resize-image)
      (save-thumbnail-to-file id)))


(defn generate-all-thumbnails []
  (loop [pictures (walk-path)
         idx 0]
    (if (empty? pictures)
      (do
        (println (str "Generated " idx " new thumbnails!")) nil)
      (if (thumbnail-exists? (first pictures))
        (recur (rest pictures) idx)
        (do
          (println (str "New picture found: " (first pictures)))
          (generate-thumbnail (first pictures))
          (recur (rest pictures) (inc idx)))))))


