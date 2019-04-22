(ns globear.messaging.actions)

;;TODO add doc strings
(def entities #{:PICTURE :THUMBNAIL :MARKER})

(def actions #{:DOWNLOAD :UPLOAD :RECEIVE})


;;TODO define actions

;;TODO make separate 'actions package' and put each action in a separate cljs file

(defn- is-message-valid? [message]
  (and
    (contains? actions (:action message))
    (contains? entities (:entity message))))

;;TODO load test-marker via [cljs-http.client :as http]

(defn execute [message]
  (if (is-message-valid? message)
    (case (:action message)
      :DOWNLOAD (println "I should do some downloading!")
      :UPLOAD (println "I should do some uploading!")
      :RECEIVE (println "I should do some state updates!"))))