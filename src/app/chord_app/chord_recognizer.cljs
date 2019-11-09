(ns app.chord-app.chord-recognizer
  (:require
   [app.chord-app.consts :refer [notes]]))

(defn append-to-string
  [old-string appendable]
  (str old-string " " appendable))

(defn chord-recognizer [chord]
  "Returns human understandable chord notation such as E min 9"
  (let [intervals (get chord :intervals)
        root (get chord :root)
        chord-string (atom (get notes (get chord :root)))
        ]
    (if (some #{3} intervals)
        (swap! chord-string append-to-string "min"))
    (if (some #{4} intervals)
        (swap! chord-string append-to-string "maj"))
    (if (some #{1} intervals)
        (swap! chord-string append-to-string "flat 9"))
    (if (some #{2} intervals)
        (swap! chord-string append-to-string "9"))
    (if (some #{5} intervals)
        (swap! chord-string append-to-string "4"))
    (if (some #{6} intervals)
        (swap! chord-string append-to-string "Trit"))
    (if (some #{8} intervals)
        (if (re-find #"min" @chord-string)
            (swap! chord-string append-to-string "6")
          (swap! chord-string append-to-string "min 6")))
    (if (some #{9} intervals)
        (if (re-find #"maj" @chord-string)
            (swap! chord-string append-to-string "6")
          (swap! chord-string append-to-string "maj 6")))
    (if (some #{10} intervals)
        (if (re-find #"min" @chord-string)
            (swap! chord-string append-to-string "7")
          (swap! chord-string append-to-string "maj 7")))
    (if (some #{11} intervals)
        (if (re-find #"maj" @chord-string)
            (swap! chord-string append-to-string "7")
          (swap! chord-string append-to-string "maj 7")))
    @chord-string))

