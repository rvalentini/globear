(ns globear.picture-overlay)


(defn close-picture-overlay [state]
  (swap! state assoc :visible false))

(defn picture-overlay [state]
  [:div
   [:div {:class "img-fullscreen-overlay" :on-click #(close-picture-overlay state)}]
   [:img {:class  "img-fullscreen" :src (:src @state)
          :on-click #(close-picture-overlay state) }]])



