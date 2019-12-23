(ns globear-backend.marker.marker-service
  (:require [globear-backend.marker.file-repository :as repo]))


(defn get-all-markers []
  (repo/load-markers-as-stream))

(defn get-marker [id]
  ;TODO implement
  )

(defn save-marker [marker]
  (repo/save-marker-to-file marker))


