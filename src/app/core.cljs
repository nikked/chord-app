(ns app.core
  (:require [reagent.core :as r]
            [app.chord-app.app :as chord-app]))

(defn ^:dev/after-load start
  []
  (r/render-component [chord-app/app]
                      (.getElementById js/document "app")))

(defn ^:export main
  []
  (start))
