(ns app.chord-app.chord-generator)

(defn get-empty-string-note [instrument string-index]
  (mod
   (+ (get instrument :root) (get (get instrument :intervals) string-index))
   12))

(defn get-valid-notes-on-string [target-chord empty-string-note]
  (reduce
   (fn [result target-interval]
     (conj result
           (mod
            (- (+ target-interval (get target-chord :root)) empty-string-note)
            12)))
   [] (get target-chord :intervals)))

(defn chord-is-finished [target-chord instrument result-chord]
  (or (=
       (count result-chord)
       (count (get instrument :intervals)))
      (>=
       (count (filter #(>= % 0) result-chord))
       (count (get target-chord :intervals)))))

(defn get-intervals-of-chord [target-chord instrument result-chord]
  (loop [iteration 0
         notes []]
    (let [new-notes
          (if (< (get result-chord iteration) 0)
              notes  (conj notes
                           (mod (+
                                 (get result-chord iteration)
                                 (get (get instrument :intervals) iteration)
                                 (-
                                  (get instrument :root)
                                  (get target-chord :root))
                                 )
                                12)))]
      (if (< iteration (- (count result-chord) 1))
          (recur (inc iteration) new-notes)
        new-notes))))

(defn chord-is-valid [target-chord instrument result-chord]
  (and (> 4
          (- (apply max result-chord)
             (apply min (filter #(> % 0) (conj result-chord 100)))))
       (= (count (filter #(>= % 0 ) result-chord)) (count (get target-chord :intervals)))
       (= (set (get target-chord :intervals)) (set (get-intervals-of-chord target-chord instrument result-chord)))
       ))


(defn add-chord
  [chords-coll new-chord]
  (conj chords-coll new-chord))

(defn handle-tab-number [tab-number]
  (if (or (not tab-number) (< tab-number 0))
      "-" (str tab-number)
    )

  )


(defn format-tab [chord-tab instrument]
  (loop [iteration 0
         result []]
    (if (>= iteration (count (:intervals instrument)))
        result
      (recur (inc iteration) (conj result (handle-tab-number (get chord-tab iteration))))
      )
    )
  )

(defn get-interval-label [interval]
  (get {
        0 "I"
        1 "II"
        2 "II"
        3 "III"
        4 "III"
        5 "IV"
        6 "Tr"
        7 "V"
        8 "VI"
        9 "VI"
        10 "VII"
        11 "VII"
        }
       interval
       )
  )

(defn get-note-label [note]
  (get {
        0 "C"
        1 "C#"
        2 "D"
        3 "D#"
        4 "E"
        5 "F"
        6 "F#"
        7 "G"
        8 "G#"
        9 "A"
        10 "Bb"
        11 "H"
        }
       note
       )
  )


(defn get-tab-intervals [chord-tab instrument target-chord]
  (loop [iteration 0
         result []]
    (if (>= iteration (count (:intervals instrument)))
        result
      (let [tab-number (get chord-tab iteration)]
        (recur
         (inc iteration)
         (if (and tab-number (> tab-number -1))
             (conj result
                   (get-interval-label
                    (mod
                     (+ 12 tab-number (get instrument :root)
                        (get (get instrument :intervals) iteration)
                        (- (get target-chord :root))
                        ) 12)))
           (conj result "-")))))))


(defn get-tab-notes [chord-tab instrument]
  (loop [iteration 0
         result []]
    (if (>= iteration (count (:intervals instrument)))
        result
      (let [tab-number (get chord-tab iteration)]
        (recur
         (inc iteration)
         (if (and tab-number (> tab-number -1))
             (conj result
                   (get-note-label
                    (mod
                     (+ 12 tab-number (get instrument :root)
                        (get (get instrument :intervals) iteration)
                        ) 12)))
           (conj result "-")))))))


(defn get-note-numbers [chord-tab instrument]
  (loop [iteration 0
         result []]
    (if (>= iteration (count (:intervals instrument)))
        result
      (let [tab-number (get chord-tab iteration)]
        (recur
         (inc iteration)
         (if (and tab-number (> tab-number -1))
             (conj result (+ (:root instrument)
                             (get (get instrument :intervals) iteration)
                             (get chord-tab iteration))) result
           ))))))


  (defn chords-coll-formatter [chords-coll instrument target-chord]
    (reduce
     (fn [result chord-tab]
       (conj result {:note-numbers (get-note-numbers chord-tab instrument)
                     :intervals (get-tab-intervals chord-tab instrument target-chord)
                     :notes (get-tab-notes chord-tab instrument)
                     :tab (format-tab chord-tab instrument)})
       )
     [] chords-coll)
    )

  (defn chord-generator

    ; default chord: E min 6
    ; default instrument: Standard Guitar
    ([] (chord-generator {:root 4  :intervals [0 3 7 9] }))
    ([target-chord](chord-generator target-chord {:root 4 :intervals [0 5 10 15 19 24]}))

    ([target-chord instrument]
     (let [chords-coll (atom [])]
       (chord-generator target-chord instrument [] chords-coll)
       (chords-coll-formatter @chords-coll instrument target-chord)))

    ([target-chord instrument result-chord chords-coll]
     (if (chord-is-finished target-chord instrument result-chord)
         (when (chord-is-valid target-chord instrument result-chord)
           (swap! chords-coll add-chord result-chord))
       (do
        (doseq [note (get-valid-notes-on-string
                      target-chord
                      (get-empty-string-note
                       instrument
                       (count result-chord)))]
          (chord-generator target-chord instrument (conj result-chord note) chords-coll))
        (if (or (empty? result-chord) (= -1 (last result-chord)))
            (chord-generator target-chord instrument (conj result-chord -1) chords-coll))))))
