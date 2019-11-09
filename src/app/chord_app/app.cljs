(ns app.chord-app.app
  (:require [reagent.core :as r :refer [render]]
            [clojure.string :as s :refer [trim blank?]]
            [cljs.reader :refer [read-string]]
            [app.chord-app.chord-recognizer :refer [chord-recognizer]]
            [app.chord-app.chord-generator :refer [chord-generator]]
            [app.chord-app.consts :refer [tone-js-pitches notes instruments]]
            ["@tombatossals/react-chords/lib/Chord" :default guitar-chord-tab]
            ["tone" :as tone]
            ))

(def chord-data (r/atom  {:intervals #{0 7 2 10}
                          :root 4}))

(def enable-sound (r/atom false))

(def instrument-data (r/atom  (:std_guitar instruments)))

(defn get-synth []
  (let [synth (tone/Synth.)]
    (.toMaster synth)))

(defn play-chord [notes]
  (if @enable-sound
      (let [timeout (atom 0)]
        (doseq [note notes]
          (js/setTimeout
           #(.triggerAttackRelease (get-synth) (get tone-js-pitches note) "1")
           @timeout)
          (swap! timeout #(+ 110 %))))
    ))

(defn play-default-chord []
  (let [notes (sort (map #(+ 12 % (:root @chord-data)) (:intervals @chord-data)))]
    (play-chord notes)))

(defn handle-note-dropdown-on-click [new-root]
  (let [intervals (:intervals @chord-data)]
    (reset! chord-data {:intervals intervals
                        :root new-root})))

(defn handle-instrument-dropdown-on-click [new-instrument]
  (reset! instrument-data (get instruments new-instrument)))

(defn handle-interval-on-click [clicked-interval]
  (let [intervals (:intervals @chord-data)
        root (:root @chord-data)]
    (if (contains? intervals clicked-interval)
        (reset! chord-data {:intervals (disj intervals clicked-interval)
                            :root root})
      (reset! chord-data {:intervals (conj intervals clicked-interval)
                          :root root}))))

(defn chord-item-on-click [chord]
  (let [notes (sort (map #(+ % (:root chord)) (:note-numbers chord)))]
    (play-chord notes)
    ))

(defn enable-sound-button-on-click []
  (reset! enable-sound (not @enable-sound))
  )

(defn set-interval-button-class [value]
  (let [intervals (:intervals @chord-data)]
    (if (contains? intervals value)
        "btn btn-primary" "btn btn-secondary")))


(defn render-chord-name []
  (let [chord-name (chord-recognizer @chord-data)
        chord-variations (chord-generator @chord-data)]
    [:div {:class "col-md-5 text-center chord-name-style"}
     [:h1 chord-name]
     [:h3 (str (count chord-variations) " variations")]]))


(defn render-instrument-dropdown []
  [:div {:class "instrument-dropdown-style"}
   [:h5 "Instrument"]
   [:div {:class "dropdown"}
    [:button {:class "btn btn-secondary dropdown-toggle"
              :type "button"
              :id "dropdownMenuButton"
              :data-toggle "dropdown"
              :aria-haspopup "true"
              :aria-expanded "false"}
     (:name @instrument-data)]
    [:div {
           :class "dropdown-menu"
           :aria-labelledby "dropdownMenuButton"}
     (for [instrument (keys instruments)]
       [:a {:class "dropdown-item" :on-click #(handle-instrument-dropdown-on-click instrument)} (:name (instrument instruments))])
     ]]])


(defn render-note-dropdown []
  [:div
   [:h5 "Root note"]
   [:div {:class "dropdown"}
    [:button {:class "btn btn-secondary dropdown-toggle"
              :type "button"
              :id "dropdownMenuButton"
              :data-toggle "dropdown"
              :aria-haspopup "true"
              :aria-expanded "false"}
     (str (get notes (:root @chord-data)))]
    [:div {
           :class "dropdown-menu"
           :aria-labelledby "dropdownMenuButton"}
     (for [note notes]
       [:a {:class "dropdown-item" :on-click #(handle-note-dropdown-on-click (.indexOf notes note))} note]
       )
     ]]])

(defn render-selectors []
  [:div {:class "col-md-3 col-sm-6 text-center selectors-style"}
   (render-note-dropdown)
   (render-instrument-dropdown)
   ]
  )

(defn render-button [value label]
  [:button {:on-click #(handle-interval-on-click value)
            :type "button"
            :class (set-interval-button-class value)
            :value value
            } label])

(defn render-intervals-button-group []
  [:div {:class "col-md-4 col-sm-6 text-center"}
   [:h5 "Neutral intervals"]
   [:div {:class "btn-group interval-button-group" :role "group"}
    [render-button 2 "II"]
    [render-button 5 "IV"]
    [render-button 7 "V"]]
   [:h5 "Major intervals"]
   [:div {:class "btn-group interval-button-group" :role "group"}
    [render-button 4  "III"]
    [render-button 9  "VI"]
    [render-button 11  "VII"]]
   [:h5 "Minor intervals"]
   [:div {:class "btn-group interval-button-group" :role "group"}
    [render-button 3  "III"]
    [render-button 8  "VI"]
    [render-button 10  "VII"]
    ]
   [:h5 "Dissonant intervals"]
   [:div {:class "btn-group interval-button-group" :role "group"}
    [render-button 1 "Flat second"]
    [render-button 6  "Tritonus"]]])

(defn render-tab-row [tab-vector]
  [:div {:class "row tab-row-style"}
   (map (fn [note] [:div {:class "col-2 tab-row-item-style" :key (rand)} (str note)]) tab-vector)
   ]
  )


(defn render-tab-chart [renderable-tab base-fret]
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

(defn render-tab [chord]
  (let [tab-as-int (map #(if (or (= % "-") (= % "0")) 99
                          (read-string %)) (:tab chord))]
    (let [min-finger (apply min (filter #(not (= 99 %)) tab-as-int))
          max-finger (apply max (filter #(not (= 99 %)) tab-as-int))]
      (if (< max-finger 4)
          (render-tab-chart (map #(if (= % "-") -1
                                   (read-string %))
                                 (:tab chord) ) 0)

        (render-tab-chart (map #(if (= % "-") -1
                                 (max (- (read-string %) min-finger -1) 0))
                               (:tab chord) ) min-finger)))))


(defn render-chord-grid-item [chord]
  [:div {:class "card chord-grid-item-style"
         :on-click #(chord-item-on-click chord)}
   [:div {:class "card-body"}
    (render-tab-row (:notes chord))
    (render-tab-row (:intervals chord))
    (render-tab-row (:tab chord))
    [render-tab chord]]])

(defn render-chord-grid []
  (let [chord-variations (chord-generator @chord-data @instrument-data)]
    [:div {:class "row chord-grid-style"}
     (map (fn [chord]
            [:div {:class "col-lg-2 col-md-3 col-sm-4 col-6" :key (str chord)}
             (render-chord-grid-item chord)]) chord-variations)]))

(defn render-enable-sound-button []
  (if @enable-sound
      [:button {:type "button"
                :class "btn btn-primary"
                :onClick #(enable-sound-button-on-click)}
       "Disable sound"]
    [:button {:type "button"
              :class "btn btn-primary"
              :onClick #(enable-sound-button-on-click)}
     "Enable sound"]))


(defn app []
  (play-default-chord)
  [:div {:class "container"}
   (render-enable-sound-button)
   [:div {:class "row"}
    [render-chord-name]
    [render-selectors]
    [render-intervals-button-group]]
   [render-chord-grid]
   ])
