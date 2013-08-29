(ns clj-uclassify.core-test
  (:require [clojure.test :refer :all]
            [clj-uclassify.uclassify :refer :all]))

(deftest canary-test
  (testing "Canary test"
    (is (= 0 0))))

(def ^:private api-keys {:read-key "aD02ApbU29kNOG2xezDGXPEIck" :write-key "fsqAft7Hs29BgAc1AWeCIWdGnY"})
(def ^:private classifer-name (str (System/currentTimeMillis)))

(deftest uclassify-test
  (testing "API test with two classes"
    (is (= (create-classifier api-keys classifer-name) true) "Create New Classifer")
    (is (= (add-class api-keys classifer-name '("class1" "class2")) true) "Creates some new class labels in Classifier")
    (is (= (get-information api-keys classifer-name) '(("class1" "class2") ("0" "0") ("0" "0"))) "get information about classifier")
    (is (= (classify api-keys '("hi" "bye") classifer-name)
           '(("0" "0") (["class1" "0.5"] ["class2" "0.5"]) (["class1" "0.5"] ["class2" "0.5"])))
        "Classify the passed texts")
    (is (= (classify-keywords api-keys '("hi" "bye") classifer-name)
           '(("0" "0") (["class1" "0.5" ""] ["class2" "0.5" ""]) (["class1" "0.5" ""] ["class2" "0.5" ""]))))
    (is (= (classify api-keys '("that cosmetic is so nice" "I like to code.") "GenderAnalyzer_v5" "uClassify")
           '(("1" "1") (["female" "0.844033"] ["male" "0.155967"]) (["female" "0.146065"] ["male" "0.853935"])))
        "Classify using predefined classifier which is already present")
    (is (= (classify-keywords api-keys '("that cosmetic is so nice" "I like to code") "GenderAnalyzer_v5" "uClassify")
           '(("1" "1") (["female" "0.844033" "cosmetic so nice"] ["male" "0.155967" "is that"]) (["female" "0.285969" "I like to"] ["male" "0.714031" "code"]))))
    (is (= (train api-keys '("I like cosmetic" "That is so hot") "class1" classifer-name) true) "Train a classifier")
    (is (= (untrain api-keys '("I like cosmetic" "That is so hot") "class1" classifer-name) true) "UnTrain a classifier")
    (is (= (remove-class api-keys classifer-name '("class1" "class2")) true) "Removes the created class labels")
    (is (thrown? Throwable (create-classifier api-keys classifer-name)) "Creating Existing classifier")
    (is (= (remove-classifier api-keys classifer-name) true) "Removes created classifier")))

(def ^:private another-classifier (str classifer-name \a))

(deftest another-uclassify-test
  (testing "Another API test with three classes"
    (is (= (create-classifier api-keys another-classifier) true) "Create New Classifer")
    (is (= (add-class api-keys another-classifier '("class1" "class2" "class3")) true) "Creates some new class labels in Classifier")
    (is (= (get-information api-keys another-classifier)
           '(("class1" "class2" "class3") ("0" "0" "0") ("0" "0" "0"))) "get information about classifier")
    (is (= (classify api-keys '("hi" "bye") another-classifier)
           '(("0" "0") (["class1" "0.333333"] ["class2" "0.333333"] ["class3" "0.333333"]) (["class1" "0.333333"] ["class2" "0.333333"] ["class3" "0.333333"]))) "Classify the passed texts")
    (is (= (classify-keywords api-keys '("hi" "bye") another-classifier)
           '(("0" "0") (["class1" "0.333333" ""] ["class2" "0.333333" ""] ["class3" "0.333333" ""]) (["class1" "0.333333" ""] ["class2" "0.333333" ""] ["class3" "0.333333" ""]))))
    (is (= (train api-keys '("I like cosmetic" "That is so hot") "class1" another-classifier) true) "Train a classifier")
    (is (= (untrain api-keys '("I like cosmetic" "That is so hot") "class1" another-classifier) true) "UnTrain a classifier")
    (is (= (remove-class api-keys another-classifier '("class1" "class2")) true) "Removes the created class labels")
    (is (thrown? Throwable (create-classifier api-keys another-classifier)) "Creating Existing classifier")
    (is (= (remove-classifier api-keys another-classifier) true) "Removes created classifier")))
