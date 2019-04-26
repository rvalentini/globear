(ns globear.messaging.actions
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<! >!]]
            [globear.messaging.channel :as channel]
            [globear.map :as map]))

;;TODO add doc strings
(def entities #{:PICTURE :THUMBNAIL :MARKER})
(def actions #{:DOWNLOAD :UPLOAD :RECEIVE})

;;TODO create picture DOWNLOAD action next
;;TODO make separate 'actions package' and put each action in a separate cljs file

(defn- is-message-valid? [message]
  (and
    (contains? actions (:action message))
    (contains? entities (:entity message))))



;;TODO this should be in a separate cljs donwload action file
(defn- download-marker-action []
  (go (let [response (<! (http/get "http://localhost:3000/marker"
                                   {:with-credentials? false}))]
        (as-> response r
            (js->clj r)
            (>! channel/response-chan {:action :RECEIVE :entity :MARKER :payload (:body r)})))))

(defn- receive-marker-action [message]
  ;;TODO modify some reagent/atom containing markers and do not call add-markers manually
  (println "I should do some real state update somewhere in globear.map.cljs")
  (map/add-marker-to-map (:payload message)))




(defn- download [message]
  (case (:entity message)
    :MARKER (download-marker-action) ;;TODO give optional specific marker as parameter
    :PICTURE (println "I should donwload some pictures!")
    :THUMBNAIL(println "I should donwload some thumbnails!")))

(defn- receive [message]
  (case (:entity message)
    :MARKER (receive-marker-action message)
    :PICTURE (println "I should donwload some pictures!")
    :THUMBNAIL(println "I should donwload some thumbnails!"))
  )




(defn execute [message]
  (if (is-message-valid? message)
    (case (:action message)
      :DOWNLOAD (download message)
      :UPLOAD (println "I should do some uploading!")
      :RECEIVE (receive message))))




