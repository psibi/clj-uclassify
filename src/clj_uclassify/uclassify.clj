(ns clj-uclassify.uclassify
  (:require [clj-uclassify.core :refer :all]
            [clojure.data.xml :as xml]
            [clojure.zip :as zip])
  (:use [clojure.string :only (join)]))

(defn create-classifier [keys classifier]
  (if (check-keys keys)
    (post-request
     (xml/emit-str
      (zip/root
       (zip/append-child
        (zip/xml-zip uclassify)
        (make-xml-node :writeCalls {:writeApiKey (keys :write-key) :classifierName classifier}
                       (make-xml-node :create {:id "Create"}))))))
    (throw (Throwable. "API key not found"))))

(defn add-class
  "Adds class to the existing classifier"
  [keys classifier class-name]
  (if (check-keys keys)
    (let [xml-elements (map #(make-xml-node :addClass
                                            {:id (join (seq ["AddClass" %]))
                                             :className %}) class-name)
          write-calls (make-xml-node :writeCalls
                                     {:writeApiKey (keys :write-key)
                                      :classifierName classifier}
                                     xml-elements)
          final-xml (zip/root (zip/insert-child
                               (zip/xml-zip uclassify)
                               (zip/xml-zip write-calls)))]
      (post-request
       (xml/emit-str final-xml)))
    (throw (Throwable. "API Key not found"))))

(defn remove-classifier
  "Removes pre-existing classifier"
  [keys classifier]
  (if (check-keys keys)
    (post-request
     (xml/emit-str
      (zip/root
       (zip/append-child
        (zip/xml-zip uclassify)
        (make-xml-node :writeCalls {:writeApiKey (keys :write-key) :classifierName classifier}
                       (make-xml-node :remove {:id "Remove"}))))))
    (throw (Throwable. "API key not found"))))

(defn remove-class
  "Removes class from the existing classifier"
  [keys classifier class-name]
  (if (check-keys keys)
    (let [xml-elements (map #(make-xml-node :removeClass
                                            {:id (join (seq ["RemoveClass" %]))
                                             :className %}) class-name)
          write-calls (make-xml-node :writeCalls
                                     {:writeApiKey (keys :write-key)
                                      :classifierName classifier}
                                     xml-elements)
          final-xml (zip/root (zip/insert-child
                               (zip/xml-zip uclassify)
                               (zip/xml-zip write-calls)))]
      (post-request
       (xml/emit-str final-xml)))
    (throw (Throwable. "API Key not found"))))


(defn append-elements
  "Pass an xml-node and an sequence of xml-node, it will return the appended xml-node"
  [xml-node xml-nodes]
  (zip/root
   (reduce #(zip/append-child %1 %2) (zip/xml-zip xml-node) xml-nodes)))
