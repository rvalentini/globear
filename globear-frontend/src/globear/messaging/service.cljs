(ns globear.messaging.service
  (:require [cljs.core.async :refer [>! <! go chan buffer close! alts! timeout]]
            [globear.messaging.channel :as channels]
            [globear.messaging.actions :as actions]))


(defn request-worker []
  (println "Starting request worker...")
  (go (while true
        (let [message (<! channels/request-chan)]
          (go (actions/execute message))))))


(defn response-worker []
  (println "Starting response worker...")
  (go (while true
        (let [message (<! channels/response-chan)]
          (go (actions/execute message))))))


