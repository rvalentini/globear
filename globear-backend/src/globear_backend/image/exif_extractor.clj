(ns globear-backend.image.exif-extractor
  (:require [exif-processor.core :as exif]))


(defn extract-exif-meta-data [img-file]
  (let [exif (exif/exif-for-file img-file)]
    (println exif)
    )

  )


