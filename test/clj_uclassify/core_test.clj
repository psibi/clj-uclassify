(ns clj-uclassify.core-test
  (:require [clojure.test :refer :all]
            [clj-uclassify.uclassify :refer :all]))

(deftest canary-test
  (testing "Canary test"
    (is (= 0 0))))

(def api-keys {:read-key "aD02ApbU29kNOG2xezDGXPEIck" :write-key "fsqAft7Hs29BgAc1AWeCIWdGnY"})
(def classifer-name (str (System/currentTimeMillis)))

(deftest create-classifier-test
  (testing "Classifier test"
    (is (= (create-classifier api-keys classifer-name) true) "Create New Classifer")
    (is (= (add-class api-keys classifer-name '("class1" "class2")) true) "Creates some new class labels in Classifier")
    (is (= (get-information api-keys classifer-name) '(("class1" "class2") ("0" "0") ("0" "0"))) "get information about classifier")
    (is (= (train api-keys '("I like cosmetic" "That is so hot") "class1" classifer-name) true) "Train a classifier")
    (is (= (untrain api-keys '("I like cosmetic" "That is so hot") "class1" classifer-name) true) "UnTrain a classifier")
    (is (= (remove-class api-keys classifer-name '("class1" "class2")) true) "Removes the created class labels")
    (is (thrown? Throwable (create-classifier api-keys classifer-name)) "Creating Existing classifier")
    (is (= (remove-classifier api-keys classifer-name) true) "Removes created classifier")))
