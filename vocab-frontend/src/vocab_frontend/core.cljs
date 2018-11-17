(ns vocab-frontend.core
  (:require [reagent.core :as reagent]
            [goog.string :as gstring]))


(defn app []
      [:div
       [:h1 {:class "title"} "Here the Vocab application will live :D" ]
       [:p {:style {:font-size "400px"}} (gstring/unescapeEntities "&#14681;")]])



(reagent/render [app] (js/document.querySelector "#cljs-target"))