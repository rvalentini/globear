(ns globear-backend.thumbnail-service
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))


(defn- walk-path []
  (->> (str "resources/pictures")
       clojure.java.io/file
       file-seq
       (map #(.getName %))
       (filter #(string/includes? % ".jpg"))
       (map #(subs % 0 (- (count % ) 4)))))


(defn- thumbnail-exists? [id]
  (.exists (io/as-file (str "resources/thumbnails/" id "_thumbnail.png"))))


(defn generate-thumbnails []
  (doseq [picture (walk-path)]
    (if (thumbnail-exists? picture)
      (println (str picture " exists"))
      (println (str picture " does not exist")))))

;TODO make service run at startup
;TODO generate thumbnails if 'not exists'

