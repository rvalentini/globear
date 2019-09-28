(ns globear.map.context-menu)

(defn close [state]
  (swap! state assoc :visible false))

;TODO rename lat lng -> now position x,y (= top, left)
;TODO make nicer!
(defn context-menu [state]
  [:div {:id "context-menu" :class "context-menu" :on-click #(close state) :style {:left (str (get-in @state [:position :lat]) "px")  :top (str (get-in @state [:position :lng]) "px")}}
   [:ul
    [:li
     [:a {:on-click #(js/alert (str "Current location: " (:position @state)))} ;TODO call add-new-marker
      "New marker"]]
    [:li
     [:a {:on-click #(js/alert "You clicked 2")}
      "Something else"]]
    ]
   ]

  )