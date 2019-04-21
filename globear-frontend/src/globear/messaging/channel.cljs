(ns globear.messaging.channel
  (:require [cljs.core.async
             :as a
             :refer [>! <! go chan buffer close! alts! timeout]]))

;;TODO make usable for sever communication and define proper ACTIONS

(def test-chan (chan))

(go (while true
      (println (<! test-chan))))

(defn push-message [msg]
  (go (>! test-chan msg)))


