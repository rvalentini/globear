(ns globear-backend.thumbnail-service
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [globear-backend.image.thumbnail :refer [generate-thumbnail]]))


(defn- walk-path []
  (->> (str "resources/pictures")
       clojure.java.io/file
       file-seq
       (map #(.getName %))
       (filter #(string/includes? % ".jpg"))
       (map #(subs % 0 (- (count %) 4)))))


(defn- thumbnail-exists? [id]
  (.exists (io/as-file (str "resources/thumbnails/" id "_thumbnail.png"))))


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


