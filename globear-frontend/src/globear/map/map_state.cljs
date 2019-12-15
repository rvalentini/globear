(ns globear.map.map-state
  (:require [reagent.core :as reagent]))


(def marker-source (reagent/atom nil))

(def img-overlay-state
  (reagent/atom {:visible false}))

(def context-menu-state
  (reagent/atom {:visible false
                 :position {:x 0 :y 0}
                 :coordinates {:lat 0 :lng 0}}))

(def marker-menu-state
  (reagent/atom {:visible false
                 :position {:x 0 :y 0}
                 :coordinates {:lat 0 :lng 0}}))

(def picture-popup-state
  (reagent/atom {:popup nil}))
