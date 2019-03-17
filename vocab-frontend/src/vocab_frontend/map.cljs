(ns vocab-frontend.map
  (:require [reagent.core :as reagent]
            [hiccups.runtime :as hiccupsrt])
  (:require-macros [hiccups.core :as hiccups :refer [html]]))


(def map (reagent/atom {:map nil} ))                        ;;TODO nested is not good

(defn- create-popup-for-marker [marker]
  (-> (new js/mapboxgl.Popup (clj->js {:offset 25}))
      (.setHTML (html [:div [:h3 (aget marker "properties" "title")] [:p (aget marker "properties" "description")]]) )))

;;TODO concept for max-size and resizing of pictures (see https://stackoverflow.com/questions/19192892/css-how-can-i-set-image-size-relative-to-parent-height)
;;TODO a single marker should be able to hold multiple pictures
(defn- create-popup-with-pic [marker]
  (-> (new js/mapboxgl.Popup (clj->js {:offset 25}))
      (.setHTML (html [:img {:src (aget marker "properties" "picture") :width 170 :height 100}]) )))

(defn add-marker [marker]
  (let [element (.createElement js/document "div")]
    (set! (.-className element) "marker" )
    (-> (new js/mapboxgl.Marker element)
        (.setLngLat (aget marker "geometry" "coordinates"))
        (.setPopup (create-popup-with-pic marker))
        (.addTo (:map @map)))))



(defn- map-init []
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (swap! map assoc :map (new js/mapboxgl.Map (clj->js {:container "map"
                                            :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1"
                                            :center [-122.403944, 37.784020],
                                            :zoom 10.6})))
    (.addControl (:map @map) (new js/mapboxgl.NavigationControl))
    (add-marker (clj->js { :type "Feature" :geometry {:type "Point" :coordinates [-122.416629 37.787135]} :properties {:title "Mapbox" :description "blah" :picture "some_food.jpg"}})))



(defn- map-render []
  [:div#map])

(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))






