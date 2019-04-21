(ns globear.messaging.service
  (:require [cljs.core.async
             :as a
             :refer [>! <! go chan buffer close! alts! timeout]]
            [globear.messaging.channel :as channels]
            [globear.messaging.actions :as actions]))


(defn request-worker []
  (go (while true
        (let [message (<! channels/request-chan)]
          (go (actions/execute message))

          ;;TODO check which type of server request -> DONE in actions
          ;;TODO execute server request in new go block
          ;;TODO write answer to response worker's queue
          ))))


(defn response-worker []
  (go (while true
        (let [message (<! channels/response-chan)]
          (go (actions/execute message))
          ;;TODO check which type of server request
          ;;TODO execute server request in new go block
          ;;TODO write answer to response worker's queue
          ))))



