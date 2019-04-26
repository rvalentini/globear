(ns globear.messaging.service
  (:require [cljs.core.async
             :as a
             :refer [>! <! go chan buffer close! alts! timeout]]
            [globear.messaging.channel :as channels]
            [globear.messaging.actions :as actions]))

;;TODO remove prints and add some nice doc string

(defn request-worker []
  (println "Starting request worker...")
  (go (while true
        (let [message (<! channels/request-chan)]
          (println "Request worker picked up a message!")
          (go (actions/execute message))))))


(defn response-worker []
  (println "Starting response worker...")
  (go (while true
        (let [message (<! channels/response-chan)]
          (println "Response worker picked up a message!")
          (go (actions/execute message))))))


