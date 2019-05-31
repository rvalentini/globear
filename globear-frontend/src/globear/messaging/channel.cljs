(ns globear.messaging.channel
  (:require [cljs.core.async
             :as a
             :refer [>! <! go chan buffer close! alts! timeout]]))


(def request-chan
  "The request-chan channel is used for all communication from frontend to backend.
  Action.cljs defines all possible actions that can be pushed into this channel.
  Every message inserted is picked up by the request-worker (defined in messaging.service.cljs)
  and executed asynchronously. "
  (chan))

(def response-chan
  "The response-chan channel is used for all communication from backend to frontend,
  meaning that responses from the backend are buffered in this channel and then handled
  by the response-worker (defined in messaging.service.cljs). "
  (chan))




