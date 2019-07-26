(ns globear.messaging.actions
  (:require [cljs.core.async :refer [<! >! go]]
            [globear.messaging.channel :as channel]
            [globear.map :as map]
            [cljs-http.client :as http]
            [cognitect.transit :as transit]))

(def reader (transit/reader :json))

(defmulti execute
          (fn [x] [(:action x) (:entity x)]))



(defmethod execute [:download :marker] [_]
  (println "Requested all markers from backend")
  (go (let [response (<! (http/get "http://localhost:3000/geojson" {:with-credentials? false}))]
        (>! channel/response-chan {:action :receive :entity :markers :payload (:body response)}))))


(defmethod execute [:download :picture] [message]
  (println "Requested picture from backend")
  (let [url (str "http://localhost:3000/pictures/" (:id message))] ;;TODO make URL configurable
    (go (let [_ (<! (http/get url
                                     {:with-credentials? false}))]
          (>! channel/response-chan {:action :receive :entity :picture :id (:id message) :url url})))))

(defmethod execute [:download :thumbnail] [message]
  (println "Requested thumbnail from backend")
  (let [url (str "http://localhost:3000/thumbnails/" (:id message))] ;;TODO make URL configurable
    (go (let [_ (<! (http/get url
                                     {:with-credentials? false}))]
          (>! channel/response-chan {:action :receive :entity :thumbnail :id (:id message) :url url})))))




(defmethod execute [:receive :markers] [message]
  (map/add-markers-source-to-map (:payload message) ))

(defmethod execute [:receive :picture] [message]
  (let [interval (atom nil)]
    (reset! interval (.setInterval
                       js/window
                       (fn []
                         (when (some? (.getElementById js/document "img-overlay"))
                           (do
                             (.clearInterval js/window @interval)
                             (-> js/document
                                 (.getElementById "img-overlay")
                                 (.-src)
                                 (set! (:url message))))))
                       10))))

(defmethod execute [:receive :thumbnail] [message]
  (let [interval (atom nil)]
    (reset! interval (.setInterval
                       js/window
                       (fn []
                         (when (some? (.getElementById js/document (:id message)))
                           (do
                             (.clearInterval js/window @interval)
                             (-> js/document
                                 (.getElementById (:id message))
                                 (.-src)
                                 (set! (:url message))))))
                       10))))




