(ns globear.image-overlay)


(defn close-image-overlay [state]
  (swap! state assoc :visible false))

(defn image-overlay [state]
  [:div
   [:div {:class "img-fullscreen-overlay" :on-click #(close-image-overlay state)}]
   [:img {:class  "img-fullscreen" :src (:src @state)
          :on-click #(close-image-overlay state) }]])



