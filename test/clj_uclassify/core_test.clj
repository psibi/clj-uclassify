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
    (is (thrown? Throwable (create-classifier api-keys classifer-name)) "Creating Existing classifier")))
