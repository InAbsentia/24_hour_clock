(ns clock.face)

(def state (atom {}))

(defn window [] (let [window js/window] window))

(defn canvas [] (let [canvas (.querySelector js/document "#face")] canvas))

(defn windowWidth [] (.-innerWidth (window)))

(defn windowHeight [] (.-innerHeight (window)))

(defn size [] (* (min (windowWidth) (windowHeight)) 0.9))

(defn width [] (let [width (. (canvas) -width)] width))

(defn height [] (let [height (. (canvas) -height)] height))

(defn center [canvas]
  (let [center {:x (/ (width) 2), :y (/ (height) 2)}]
    center))

(defn radius [] (let [radius (/ (- (width) 5) 2)] radius))

(defn canvasContext []
  (let [canvasContext (.getContext (canvas) "2d")]
    canvasContext))


(defn tau [] (let [tau (* 2 Math/PI)] tau))

(defn zero [] (let [zero (/ (* 3 (tau)) 4)] zero))

(defn rightNow [] (js/Date.))

(defn angleOffset [angle]
  (+ (zero) angle))

(defn hoursFraction [date]
  (/ (.getHours date) 24))

(defn minutesFraction [date]
  (/ (+ (.getMinutes date)) 60))

(defn secondsFraction [date]
  (/ (+ (.getSeconds date)
        (/ (.getMilliseconds date) 1000))
     60))

(defn hoursAngle [date]
  (* (tau)
     (+ (hoursFraction date)
        (/ (minutesFraction date) 24))))

(defn minutesAngle [date]
  (* (tau) (minutesFraction date)))

(defn secondsAngle [date]
  (* (tau) (secondsFraction date)))


(defn onCircle [center angle radius]
  {:x (+ (center :x) (* (Math/cos angle) radius)),
   :y (+ (center :y) (* (Math/sin angle) radius))})

(defn drawCircle [context center radius]
  (.beginPath context)
  (.arc context (center :x) (center :y) radius (tau) false)
  (.stroke context))

(defn fillCircle [context center radius]
  (.beginPath context)
  (.arc context (center :x) (center :y) radius (tau) false)
  (.fill context))

(defn drawTick [number context center radius]
  (def angle (+ (zero) (* (tau) (/ number 60))))
  (def point (onCircle center angle radius))

  (.save context)

  (.translate context (point :x) (point :y))
  (.rotate context angle)

  (.moveTo context 0 0)
  (.lineTo context -5 0)

  (set! (.-lineWidth context) 1)
  (.stroke context)

  (.restore context))

(defn drawTicks [context center radius]
  (doseq [number (range 60)]
    (drawTick number context center (- radius 2))))

(defn drawNumber [number context center radius total]
  (.save context)

  (def angle (+ (zero) (* (tau) (/ number total))))
  (def point (onCircle center angle radius))

  (.beginPath context)
  (.translate context (point :x) (point :y))
  (.fillText context (goog.string/padNumber number, 2) 0 0)

  (.restore context))

(defn drawNumbers [context center radius]
  (set! (.-font context) "20px sans-serif")
  (set! (.-textAlign context) "center")
  (set! (.-textBaseline context) "middle")

  (doseq [number (range 24)]
    (drawNumber (+ number 1) context center (- radius 20) 24))

  (set! (.-font context) "14px sans-serif")
  (set! (.-textAlign context) "center")
  (set! (.-textBaseline context) "middle")

  (doseq [number (range 0 60 5)]
    (drawNumber number context center (- radius 45) 60)))

(defn drawFace [context center radius]
  (drawCircle context center radius)
  (fillCircle context center 7)
  (drawTicks context center radius)
  (drawNumbers context center radius))

(defn drawHourHand [context center radius]
  (def angle (angleOffset (hoursAngle (rightNow))))
  (def point (onCircle center angle (- radius 60)))

  (.beginPath context)

  (.moveTo context (center :x) (center :y))
  (.lineTo context (point :x) (point :y))

  (set! (.-lineWidth context) 7)
  (.stroke context))

(defn drawMinuteHand [context center radius]
  (def angle (angleOffset (minutesAngle (rightNow))))
  (def point (onCircle center angle (- radius 20)))

  (.save context)
  (.beginPath context)

  (.moveTo context (center :x) (center :y))
  (.lineTo context (point :x) (point :y))

  (set! (.-lineWidth context) 3)
  (.stroke context)

  (.restore context))

(defn drawSecondHand [context center radius]
  (def angle (angleOffset (secondsAngle (rightNow))))
  (def point (onCircle center angle (- radius 10)))

  (.beginPath context)

  (.moveTo context (center :x) (center :y))
  (.lineTo context (point :x) (point :y))

  (set! (.-lineWidth context) 1)
  (.stroke context))

(defn drawClock [context center radius]
  (.clearRect context 0 0 (width) (height))
  (drawFace context center radius)
  (drawHourHand context center radius)
  (drawMinuteHand context center radius)
  (drawSecondHand context center radius))


(defn updateCanvas []
  (drawClock (canvasContext) (center (canvas)) (radius)))

(defn scheduleAnimation []
  (updateCanvas)
  (def frame (js/requestAnimationFrame scheduleAnimation))
  (swap! state assoc :frame frame))


(set! (.-width (canvas)) (size))
(set! (.-height (canvas)) (size))

(scheduleAnimation)
