(ns me.arrdem.dogeon
  (:require [instaparse.core :as insta]))

(def dogeon-parser
  (insta/parser
   (slurp "doge.bnf")))

(defn read8 [& digits]
  (reduce (fn [acc d] (+ d (* 8 acc)))
          0 digits))

(defn make-number
  [{:keys [num-part
           frac-part
           expt-part]
    :or {num-part 0
         frac-part 0
         expt-part 1}
    :as args}]
  (* expt-part
     (+ frac-part
        num-part)))

(def dogeon-rewrites
  {:string (fn [& nodes]
             (->> nodes (map second) (reduce str)))

   :object (fn [& kvs]
             (into {} kvs))

   :pair   (fn [k v]
             [k v])

   :value  (fn [x]
             (cond (= "yes" x)   true
                   (= "no" x)    false
                   (= "empty" x) nil
                   true          x))

   :array (fn [& m0ar]
            (vec m0ar))

   :int   identity

   :frac  (fn [_dot & digits]
             {:frac-part (/ (apply read8 digits)
                            (reduce * (repeat (count digits) 8.0)))})

   :exp   (fn [_very & digits]
            (let [expt (apply read8 digits)]
              {:expt-part (reduce * (repeat expt 8))}))
   
   :negative (fn [x]
               (* -1 x))

   :positive (fn [& parts]
               (->> parts
                    (reduce merge)
                    make-number))

   :digit1-7 (fn [x]
               (Integer/parseInt x))

   :digit    (fn [x]
               (Integer/parseInt x))

   :digits   (fn [& m0ar]
               {:num-part (apply read8 m0ar)})

   :escaped-char (fn [_ c]
                   (case c
                     ("n")  "\n"
                     ("b")  "\b"
                     ("t")  "\t"
                     ("\"") "\""
                     ("f")  "\f"
                     ("r")  "\r"))})

(defn read-dogeon
  [input-string]
  (->> input-string
       dogeon-parser
       (insta/transform dogeon-rewrites)))
