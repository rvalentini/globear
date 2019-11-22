(ns globear-backend.marker.marker-service
  (:require [globear-backend.marker.file-repository :as repo]))


(defn get-all-markers [])
(defn get-marker [id])
(defn save-marker [marker]
  (repo/save-to-file marker))


