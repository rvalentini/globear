(ns globear.core
  (:require [reagent.core :as reagent]
            [globear.map.map :as map]
            [globear.messaging.service :as service]))


(defn- app-init []
  (service/request-worker)
  (service/response-worker))

(defn app []
  (app-init)
  [map/map-component])

(reagent/render [app] (js/document.querySelector "#cljs-target"))