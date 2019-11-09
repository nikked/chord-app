(ns app.chord-app.tab-chart
  (:require [cljs.reader :refer [read-string]]
            ["@tombatossals/react-chords/lib/Chord" :default guitar-chord-tab]))


(defn render-tab-image-with-tombatossals [renderable-tab base-fret]
  [:> guitar-chord-tab {:chord (if (> base-fret 1 )
                                   {:frets renderable-tab :baseFret base-fret}
                                 {:frets renderable-tab}
                                 )
                        :instrument {
                                     :strings 6
                                     :fretsOnChord 5
                                     :name "Guitar"
                                     :tunings {:standard []}
                                     }}])

(defn render-tab-chart [chord]
  (let [tab-as-int (map #(if (or (= % "-") (= % "0")) 99
                          (read-string %)) (:tab chord))]
    (let [min-finger (apply min (filter #(not (= 99 %)) tab-as-int))
          max-finger (apply max (filter #(not (= 99 %)) tab-as-int))]
      (if (< max-finger 4)
          (render-tab-image-with-tombatossals (map #(if (= % "-") -1
                                                     (read-string %))
                                                   (:tab chord) ) 0)

        (render-tab-image-with-tombatossals (map #(if (= % "-") -1
                                                   (max (- (read-string %) min-finger -1) 0))
                                                 (:tab chord) ) min-finger)))))

