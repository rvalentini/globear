(ns globear.messaging.actions
  (:require [cljs.core.async :refer [<! >! go]]
            [globear.messaging.channel :as channel]
            [globear.map :as map]
            [cljs-http.client :as http]
            [cognitect.transit :as transit]))

(def reader (transit/reader :json))


(defmulti execute
          (fn [x] [(:action x) (:entity x)]))


;(transit/read reader r)
;;(println r)
;(let [marker (get-in r ["features" 0  "properties" "coordinates" 0] )]
;  (println marker))


(defn- get-all-markers [message]
  (println "Requested all markers from backend")
  (go (let [response (<! (http/get "http://localhost:3000/geojson" {:with-credentials? false}))]
              (>! channel/response-chan {:action :receive :entity :markers :payload (:body response)}))))


(defn- get-single-marker [message]
  (println "Requested marker from backed")
  (go (let [response (<! (http/get (str "http://localhost:3000/markers/" (:id message))
                                   {:with-credentials? false}))]
        (as-> response r
              (js->clj r)
              (>! channel/response-chan {:action :receive :entity :marker :payload (:body r)})))))

;TODO fetch new gojson markers at once and move single marker logic to map
(defmethod execute [:download :marker] [message]
  (if (nil? (:id message))
        (get-all-markers message)
        (get-single-marker message)))


(defmethod execute [:download :picture] [message]
  (println "Requested picture from backend")
  (let [url (str "http://localhost:3000/pictures/" (:id message))] ;;TODO make configurable
    (go (let [_ (<! (http/get url
                                     {:with-credentials? false}))]
          (>! channel/response-chan {:action :receive :entity :picture :id (:id message) :url url})))))


(defmethod execute [:download :thumbnail] [message]
  (println "Requested thumbnail from backend")
  (let [url (str "http://localhost:3000/thumbnails/" (:id message))] ;;TODO make configurable
    (go (let [_ (<! (http/get url
                                     {:with-credentials? false}))]
          (>! channel/response-chan {:action :receive :entity :thumbnail :id (:id message) :url url})))))



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

;TODO this will no longer be necessary with new gojson markers
(defmethod execute [:receive :markers] [message]
  (println "Parsing marker source response ...")
  (map/add-markers-source-to-map (:payload message) ))


