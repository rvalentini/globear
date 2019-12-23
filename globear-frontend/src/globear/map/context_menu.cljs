(ns globear.map.context-menu)


(defn close [state]
  (swap! state assoc :visible false))


(defn open [coordinates position state]
  (swap! state assoc :visible true)
  (swap! state assoc-in [:position :x] (first position))
  (swap! state assoc-in [:position :y] (second position))
  (swap! state assoc-in [:coordinates :lat] (first coordinates))
  (swap! state assoc-in [:coordinates :lng] (second coordinates)))


(defn context-menu [state add-new-marker globear-map marker-source]
  [:div {:id "context-menu"
         :class "menu"
         :on-click #(close state) :style {:left (str (get-in @state [:position :x]) "px")
                                          :top (str (get-in @state [:position :y]) "px")}}
   [:ul
    [:li
     [:a {:on-click #(let [lat (get-in @state [:coordinates :lat])
                           lng (get-in @state [:coordinates :lng])]
                       (add-new-marker globear-map [lat lng] "SOME COMMENT" marker-source))}
      "New marker"]]
    [:li
     [:a {:on-click #(js/alert "You clicked 2")}
      "Something else"]]]])