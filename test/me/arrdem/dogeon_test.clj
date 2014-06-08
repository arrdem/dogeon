(ns me.arrdem.dogeon-test
  (:require [clojure.test :refer :all]
            [me.arrdem.dogeon :refer :all]))

(deftest parser-tests
  (is (= (read-dogeon "such \"foo\" is so \"bar\" also \"baz\" and \"fizzbuzz\" many wow")
         {"foo" ["bar" "baz" "fizzbuzz"]}))

  (is (= (read-dogeon "such \"foo\" is \"bar\" wow")
         {"foo" "bar"}))
  
  (is (= (read-dogeon "such \" \\\"such \" is \"bar\" wow")
         {" \"such " "bar"}))

  (is (= (read-dogeon "such \"foo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow wow")
         {"foo" {"shiba" "inu", "doge" true}}))

  (is (= (read-dogeon "such \"foo\" is 42 wow")
         {"foo" 34}))

  (is (= (read-dogeon "such \"foo\" is 42.3very3 wow")
         {"foo" 17600.0})))
