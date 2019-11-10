# Chord app
This repository contains the source code for the [chord app that I have hosted on my website. ](https://nikke.io/#chord_app)  It is a fun tool I built for myself to support my music hobby. In a nutshell, it makes my guitar playing more versatile and expressive. I built the tool with [ClojureScript](https://clojurescript.org/) and [reagent](https://github.com/reagent-project/reagent).

The core idea is simple: any given chord can be played in many different positions in the neck of the guitar. These variations make your playing sound a lot richer. It is amazing how different these variations can sound! To get an idea, please navigate [to the app](https://nikke.io/#chord_app) and listen to some of the variations!

### Contents

* `src/app/chord_app/app.cljs`: The main React app that is responsible for most of the rendering and interaction.
* `src/app/chord_app/chord_generator.cljs`: The main logic that recursively calculates all the chord variations
* `src/app/chord_app/chord_recognizer.cljs`: This module is responsible for creating a human-understandable name for a chord.
* `src/app/chord_app/play_chord_sound.cljs`: A ClojureScript wrapper for [Tone.js](https://github.com/Tonejs/Tone.js) that enables playing the chords
* `src/app/chord_app/tab_chart_component.cljs`: A ClojureScript wrapper for [tombatossals' react-chords](https://github.com/tombatossals/react-chords) that is responsible for printing the chord tab


#### Install dependencies
```shell
npm install
```

#### Run dev server
```shell
npm run dev
```

#### Compile an optimized version

```shell
npm run release
```


