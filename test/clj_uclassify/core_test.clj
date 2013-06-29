(ns clj-uclassify.core-test
  (:require [clojure.test :refer :all]
            [clj-uclassify.core :refer :all]))

(deftest a-test
  (testing "Canary test"
    (is (= 0 0))))

(deftest check-keys-test
  (testing "check-keys with two keys"
    (is (= (check-keys {:read-key "hi" :write-key nil})))))
