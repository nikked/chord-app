(ns app.chord-app.play-chord-sound
  (:require [app.chord-app.consts :refer [tone-js-pitches]]
            ["tone" :as tone]))

(defn get-synth []
  (let [synth (tone/Synth.)]
    (.toMaster synth)))

(defn play-chord [notes]
  (let [timeout (atom 0)]
    (doseq [note notes]
      (js/setTimeout
       #(.triggerAttackRelease (get-synth) (get tone-js-pitches note) "1")
       @timeout)
      (swap! timeout #(+ 110 %))))
  )
