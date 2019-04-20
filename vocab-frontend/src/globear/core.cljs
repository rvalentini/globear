(ns globear.core
  (:require [reagent.core :as reagent]
            [globear.map :as map]))


;;TODO rename application vocab -> ???

(defonce app-state
    (reagent/atom
      {:message "This is a test string" }))

(defn on-click []
  "will be used later :)"
  (swap! app-state assoc :message "You clicked me :P"))


(defn app []
  [map/map-component])


(reagent/render [app] (js/document.querySelector "#cljs-target"))