(ns vocab-frontend.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]))


(def map (reagent/atom nil))


(defn- expand-pic [pic]
  (println "you clicked the picture: " pic))

(defn- create-popup-with-pics [marker]
  (-> (new js/mapboxgl.Popup (clj->js {:offset 25}))
      (.setDOMContent
        (hipo/create [:div (for [src (aget marker "properties" "pictures")]
                       [:img {:class "custom-popup-item" :src src :on-click #(expand-pic src)}])]))))


(defn add-marker [marker]                                   ;;TODO move static parts (feature, point etc.) into function
  (let [element (.createElement js/document "div") marker-js (clj->js marker)]
    (set! (.-className element) "marker")
    (-> (new js/mapboxgl.Marker element)
        (.setLngLat (aget marker-js "geometry" "coordinates"))
        (.setPopup (create-popup-with-pics marker-js))
        (.addTo @map))))


(defn- map-init []
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (reset! map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1" :center [-122.403944, 37.784020], :zoom 10.6})))
  (.addControl @map (new js/mapboxgl.NavigationControl))
  (add-marker {:type "Feature" :geometry {:type "Point" :coordinates [-122.416629 37.787135]} :properties {:title "Mapbox" :description "blah" :pictures ["some_food.jpg" "bear.png" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg" "some_food.jpg"]}}))


(defn- map-render []
  [:div#map])

(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))






