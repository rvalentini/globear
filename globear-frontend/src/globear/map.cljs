(ns globear.map
  (:require [reagent.core :as reagent]
            [hipo.core :as hipo]
            [globear.image-overlay :as img]
            [globear.test-pics :as test-resource]))


(def map (reagent/atom nil))

(def img-overlay-state
  (reagent/atom {:visible false
                 :src "https://www.airc.ie/wp-content/uploads/horse-web.jpg"}))


(defn- expand-pic [src]
  (println "you clicked the picture: " src)
  (swap! img-overlay-state assoc :src src)
  (swap! img-overlay-state assoc :visible true ))


(defn- create-popup-with-pics [marker]
  (-> (new js/mapboxgl.Popup (clj->js {:offset 25}))
      (.setDOMContent
        (hipo/create [:div (for [src (aget marker "properties" "pictures")]
                       [:img {:class "custom-popup-item" :src src :on-click #(expand-pic src)}])]))))


(defn add-marker [marker]    ;;TODO move static parts (feature, point etc.) into this function
  (let [element (.createElement js/document "div") marker-js (clj->js marker)]
    (set! (.-className element) "marker")
    (-> (new js/mapboxgl.Marker element)
        (.setLngLat (aget marker-js "geometry" "coordinates"))
        (.setPopup (create-popup-with-pics marker-js))
        (.addTo @map))))


(defn- on-zoom []
  (println (str "on-zoom-called: " (.getZoom @map))))


(defn- map-init []  ;;TODO split init and config into separate methods
  (set! (.-accessToken js/mapboxgl) "pk.eyJ1Ijoicml2YWwiLCJhIjoiY2lxdWxpdHRqMDA0YWk3bTM1Mjc1dmVvYiJ9.uxBDzgwojTzU-Orq2AEUZA")
  (reset! map (new js/mapboxgl.Map (clj->js {:container "map" :style "mapbox://styles/rival/cjt705zrp0j781gn20szdi3y1" :center [103.865158 1.354875], :zoom 10.6})))
  (.addControl @map (new js/mapboxgl.NavigationControl))
  (add-marker {:type "Feature" :geometry {:type "Point" :coordinates [103.865158 1.354875]} :properties {:title "Mapbox" :description "blah" :pictures test-resource/pics}})
  (.on @map "zoomstart" #(on-zoom)))



(defn- map-render []
  [:div
   [:div#map]
   (if (true? (:visible @img-overlay-state)) [img/image-overlay img-overlay-state])])


(defn map-component []
  (reagent/create-class {:reagent-render      map-render
                         :component-did-mount map-init}))






