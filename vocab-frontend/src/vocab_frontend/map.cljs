(ns vocab-frontend.map
  (:require [reagent.core :as reagent]))


(def map (reagent/atom {:map nil} ))                        ;;TODO nested is not good

(defn add-marker [marker]
  (let [element (.createElement js/document "div")]
    (set! (.-className element) "marker" )
    (-> (new js/mapboxgl.Marker element)
        (.setLngLat (aget marker "geometry" "coordinates"))
        (.addTo (:map @map)))))                             ;;TODO add popup with picture


(defn- map-init []
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (swap! map assoc :map (new js/mapboxgl.Map (clj->js {:container "map"
                                            :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1"
                                            :center [-122.403944, 37.784020],
                                            :zoom 10.6})))
    (.addControl (:map @map) (new js/mapboxgl.NavigationControl))
    (add-marker (clj->js { :type "Feature" :geometry {:type "Point" :coordinates [-122.416629 37.787135]} :properties {:title "Mapbox" :description "blah"}})))



(defn- map-render []
  [:div#map])

(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))






