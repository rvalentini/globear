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


(defn- download-picture-action [id]
  (go (let [response (<! (http/get (str "http://localhost:3000/pictures/" id)
                                   {:with-credentials? false}))]
        ;TODO write to response channel after download is finished and update picture state
        ;TODO keep picture state in separate namespace as regaent/atom and let [img] src point to it
        ))
  )

;;TODO this should be in a separate cljs donwload action file
      (defn- download-marker-action [id]
        (go (let [response (<! (http/get (str "http://localhost:3000/markers/" id)
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
    :MARKER (download-marker-action (:id message)) ;;TODO give optional specific marker as parameter
    :PICTURE (download-picture-action (:id message))
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




