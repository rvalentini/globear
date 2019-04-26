(ns globear.core
  (:require [reagent.core :as reagent]
            [globear.map :as map]
            [globear.messaging.service :as service]))


(defonce app-state
    (reagent/atom
      {:message "This is a test string" }))

(defn on-click []
  "will be used later :)"
  (swap! app-state assoc :message "You clicked me :P"))


(defn- app-init []
  (service/request-worker)
  (service/response-worker))

(defn app []
  (app-init)
  [map/map-component])


(reagent/render [app] (js/document.querySelector "#cljs-target"))