(ns vocab-frontend.core
  (:require [reagent.core :as reagent]
            [goog.string :as gstring]))

(defonce app-state
    (reagent/atom
      {:message "Here the Vocab application will live :D "}))


(defn app []
      [:div
       [:h1 {:class "title" :on-click #(on-click)} (:message @app-state) ]
       [:p {:style {:font-size "400px"}} (gstring/unescapeEntities "&#14681;")]])


(defn on-click []
  (swap! app-state assoc :message "You clicked me :P"))


(reagent/render [app] (js/document.querySelector "#cljs-target"))