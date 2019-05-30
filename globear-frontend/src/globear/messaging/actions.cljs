(ns globear.messaging.actions
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<! >!]]
            [globear.messaging.channel :as channel]
            [globear.model.picture-store :as picture-store]
            [globear.map :as map]))

;;TODO add doc strings
(def entities #{:PICTURE :THUMBNAIL :MARKER})
(def actions #{:DOWNLOAD :UPLOAD :RECEIVE})


;;TODO make separate 'actions package' and put each action in a separate cljs file

(defn- is-message-valid? [message]
  (and
    (contains? actions (:action message))
    (contains? entities (:entity message))))


(defn- download-picture-action [id]
  (let [url (str "http://localhost:3000/pictures/" id)]     ;;TODO make configurable
    (go (let [response (<! (http/get url  ;;response can be ignore, only actual resource download is relevant
                                     {:with-credentials? false}))]
          (>! channel/response-chan {:action :RECEIVE :entity :PICTURE :id id :url url})))))


(defn- receive-picture-action [message]
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
                                 (set! (:url message))))))))))



(defn- download-marker-action [id]
  (go (let [response (<! (http/get (str "http://localhost:3000/markers/" id)
                                   {:with-credentials? false}))]
        (as-> response r
              (js->clj r)
              (>! channel/response-chan {:action :RECEIVE :entity :MARKER :payload (:body r)})))))


(defn- receive-marker-action [message]
  ;;TODO modify some reagent/atom containing markers and do not call add-markers manually
  (map/add-marker-to-map (:payload message)))




(defn- download [message]
  (case (:entity message)
    :MARKER (download-marker-action (:id message))          ;;TODO give optional specific marker as parameter
    :PICTURE (download-picture-action (:id message))
    :THUMBNAIL (println "I should donwload some thumbnails!")))

(defn- receive [message]
  (case (:entity message)
    :MARKER (receive-marker-action message)
    :PICTURE (receive-picture-action message)
    :THUMBNAIL (println "I should donwload some thumbnails!"))
  )




(defn execute [message]
  (if (is-message-valid? message)
    (case (:action message)
      :DOWNLOAD (download message)
      :UPLOAD (println "I should do some uploading!")
      :RECEIVE (receive message))))




