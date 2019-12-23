(ns globear.map.marker-menu)

(defn close [state]
  (swap! state assoc :visible false))


(defn open [coordinates position state]
  (swap! state assoc :visible true)
  (swap! state assoc-in [:position :x] (first position))
  (swap! state assoc-in [:position :y] (second position))
  (swap! state assoc-in [:coordinates :lat] (first coordinates))
  (swap! state assoc-in [:coordinates :lng] (second coordinates)))


(defn close-marker-menu [state]
  (swap! state assoc :visible false))


(defn marker-menu [state]
  [:div {:id "marker-menu"
         :class "menu"
         :on-click #(close state) :style {:left (str (get-in @state [:position :x]) "px")
                                          :top (str (get-in @state [:position :y]) "px")}}
   [:ul
    [:li
     [:a {:on-click #(js/alert "Not yet implemented :(")}
      "Add picture"]]
    [:li
     [:a {:on-click #(js/alert "Not yet implemented :(")}
      "Delete"]]]])