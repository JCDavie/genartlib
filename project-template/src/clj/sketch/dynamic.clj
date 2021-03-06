(ns sketch.dynamic
  (:require [clojure.java.shell :refer [sh]]
            ; [genartlib.algebra :refer :all]
            ; [genartlib.curves :refer :all]
            ; [genartlib.geometry :refer :all]
            ; [genartlib.random :refer :all]
            [genartlib.util :refer [set-color-mode w h]]
            [quil.core :as q]))
  ; (:import [sketch Example]))

(defn setup []
  (q/smooth)
  ; avoid some saving issues
  (q/hint :disable-async-saveframe))

(declare actual-draw)

(defn draw []
  ; disable animation, just one frame
  (q/no-loop)

  ; set color space to HSB with hue in [0, 360], saturation in [0, 100],
  ; brightness in [0, 100], and alpha in [0.0, 1.0]
  (set-color-mode)

  ; make it easy to generate multiple images
  (doseq [img-num (range 1)]
    (let [cur-time (System/currentTimeMillis)
          seed (System/nanoTime)]

      ; use "bash -c" to support my WSL setup
      (sh "bash" "-c" "mkdir versioned-code")
      (let [code-dirname (str "versioned-code/" seed)]
        (sh "bash" "-c" (str "cp -R src/ " code-dirname)))

      (println "setting seed to:" seed)
      (q/random-seed seed)

      (try
        (actual-draw)
        (catch Throwable t
          (println "Exception in draw function:" t)))

      (println "gen time:" (/ (- (System/currentTimeMillis) cur-time) 1000.0) "s")
      (let [img-filename (str "img-" img-num "-" cur-time "-" seed ".tif")]
        (q/save img-filename)
        (println "done saving" img-filename)

        ; Some part of image saving appears to be async on windows. This is lame, but
        ; for now, add a sleep to help avoid compressing partially-written files.
        (Thread/sleep 500)
        (sh "bash" "-c" (str "convert -compress lzw " img-filename " " img-filename))
        (println "done compressing")))))

(defn actual-draw []
  ; art goes here
  )
