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
  [keys class-name classifier]
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
    final-xml))

(println (xml/emit-str (add-class akeys '("hi" "bye") "some-class")))

(defn append-elements
  "Pass an xml-node and an sequence of xml-node, it will return the appended xml-node"
  [xml-node xml-nodes]
  (zip/root
   (reduce #(zip/append-child %1 %2) (zip/xml-zip xml-node) xml-nodes)))
