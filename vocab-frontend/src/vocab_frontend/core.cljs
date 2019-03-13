(ns vocab-frontend.core
  (:require [reagent.core :as reagent]
            [vocab-frontend.map :as leaflet]))

;;TODO are externs actually necessary/working here?
;;TODO how to interact with the leaflet map?
;;TODO rename application vocab -> ???

(defonce app-state
    (reagent/atom
      {:message "This is a test string" }))

(defn on-click []
  "will be used later :)"
  (swap! app-state assoc :message "You clicked me :P"))


(defn leaflet-init []
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (new js/mapboxgl.Map (clj->js {:container "map"
                                 :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1"
                                 :center [-122.403944, 37.784020],
                                 :zoom 10.6})))

(defn leaflet-render []
  [:div#map])

(defn app []
  (reagent/create-class {:reagent-render      leaflet-render
                         :component-did-mount leaflet-init}))

(reagent/render [app] (js/document.querySelector "#cljs-target"))