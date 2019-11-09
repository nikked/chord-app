(ns app.chord-app.consts)

(def tone-js-pitches
  [
   "C2" "C#2" "D2" "D#2" "E2" "F2" "F#2" "G2" "G#2" "A2" "A#2" "B2"
   "C3" "C#3" "D3" "D#3" "E3" "F3" "F#3" "G3" "G#3" "A3" "A#3" "B3"
   "C4" "C#4" "D4" "D#4" "E4" "F4" "F#4" "G4" "G#4" "A4" "A#4" "B4"
   "C5" "C#5" "D5" "D#5" "E5" "F5" "F#5" "G5" "G#5" "A5" "A#5" "B5"
   "C6" "C#6" "D6" "D#6" "E6" "F6" "F#6" "G6" "G#6" "A6" "A#6" "B6"
   ])

(def notes ["C" "C#" "D" "D#" "E" "F" "F#" "G" "G#" "A" "Bb" "H"])

(def intervals ["I" "II" "II" "III" "III" "IV" "Tr" "V" "VI" "VI" "VII" "VII"])

(def instruments {
         :std_guitar {:name "Standard guitar"
                       :intervals [0 5 10 15 19 24]
                       :root 4}
         :std_bass {:name "Standard bass"
                     :intervals [0 5 10 15]
                     :root 4}

         :drop_d_guitar {:name "Drop D guitar"
                          :intervals [0 7 12 17 21 26]
                          :root 2}})
