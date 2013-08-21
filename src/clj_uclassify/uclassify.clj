;; Copyright (C) 2013 Sibi <sibi@psibi.in>

;; This file is part of clj-uclassify.

;; clj-uclassify program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; clj-uclassify program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; clj-uclassify program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTYwithout even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with clj-uclassify.  If not, see <http://www.gnu.org/licenses/>.

(ns #^{:doc "Implements all the API functions"}
    clj-uclassify.uclassify
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as x])
  (:use [clojure.string :only (join)]
        [clojure.data.codec.base64 :only (encode)]
        [clj-uclassify.core]))

(defn create-classifier
  "Creates a new classifier"
  [keys classifier]
  {:pre [(check-keys? keys)]}
  (post-request
   (xml/emit-str
    (zip/root
     (zip/append-child
      (zip/xml-zip uclassify)
      (make-xml-node :writeCalls
                     {:writeApiKey (keys :write-key) :classifierName classifier}
                     (make-xml-node :create {:id "Create"})))))))

(defn add-class
  "Adds class to the existing classifier"
  [keys classifier class-name]
  {:pre [(check-keys? keys)]}
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
     (xml/emit-str final-xml))))

(defn remove-classifier
  "Removes pre-existing classifier"
  [keys classifier]
  {:pre [(check-keys? keys)]}
  (post-request
   (xml/emit-str
    (zip/root
     (zip/append-child
      (zip/xml-zip uclassify)
      (make-xml-node :writeCalls {:writeApiKey (keys :write-key) :classifierName classifier}
                     (make-xml-node :remove {:id "Remove"})))))))


(defn remove-class
  "Removes class from the existing classifier"
  [keys classifier class-name]
  {:pre [(check-keys? keys)]}
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
     (xml/emit-str final-xml))))

(defn get-information
  "Returns information about the classifier."
  [keys classifier]
  {:pre [(check-keys? keys)]}
  (let [info-tag (make-xml-node :getInformation
                                {:id "GetInformation"
                                 :classifierName classifier})
        read-calls (make-xml-node :readCalls
                                  {:readApiKey (keys :read-key)}
                                  info-tag)
        final-xml (zip/root (zip/insert-child
                             (zip/xml-zip uclassify)
                             (zip/xml-zip read-calls)))
        uclassify-response (raw-post-request
                            (xml/emit-str final-xml))
        resp-class-name (x/xml-> uclassify-response
                                 :readCalls
                                 :getInformation
                                 :classes
                                 :classInformation
                                 (x/attr :className))]
    (if (check-response
         (get-response uclassify-response))
      (if (empty? resp-class-name)
        ()
        (list resp-class-name
              (x/xml-> uclassify-response
                       :readCalls :getInformation :classes
                       :classInformation :uniqueFeatures x/text)
              (x/xml-> uclassify-response
                       :readCalls :getInformation :classes
                       :classInformation :totalCount x/text)
              ))
      false                             ;Will never reach here
      )))

(defn train
  "Trains the classifier on text for a specified class"
  [keys texts class-name classifier]
  {:pre [(check-keys? keys)]}
  (let [textbase64-tag (map #(make-xml-node :textBase64
                                            {:id (str "Text" (str (index-of % texts)))}
                                            (String. (encode (.getBytes %)) )) texts)
        train-tag (map #(make-xml-node :train
                                       {:id (str "Train" %) :className class-name
                                        :textId (str "Text" %) })
                       (range (count texts)))
        texts-tag (make-xml-node :texts {} textbase64-tag)
        write-calls (make-xml-node :writeCalls
                                   {:writeApiKey (keys :write-key) :classifierName classifier}
                                   train-tag)]
    (post-request
     (xml/emit-str
      (xml-append-elements uclassify (list texts-tag write-calls))))))

(defn untrain
  "Trains the classifier on text for a specified class"
  [keys texts class-name classifier]
  {:pre [(check-keys? keys)]}
  (let [textbase64-tag (map #(make-xml-node :textBase64
                                            {:id (str "Text" (str (index-of % texts)))}
                                            (String. (encode (.getBytes %)) )) texts)
        train-tag (map #(make-xml-node :untrain
                                       {:id (str "Untrain" %) :className class-name
                                        :textId (str "Text" %) })
                       (range (count texts)))
        texts-tag (make-xml-node :texts {} textbase64-tag)
        write-calls (make-xml-node :writeCalls
                                   {:writeApiKey (keys :write-key) :classifierName classifier}
                                   train-tag)]
    (post-request
     (xml/emit-str
      (xml-append-elements uclassify (list texts-tag write-calls))))))

(defn classify
  "Sends a text to a classifier and returns a classification"
  [keys texts classifier & user-name]
  {:pre [(check-keys? keys)]}
  (let [textbase64-tag (map #(make-xml-node :textBase64
                                            {:id (str "Text" (str (index-of % texts)))}
                                            (String. (encode (.getBytes %)) )) texts)
        texts-tag (make-xml-node :texts {} textbase64-tag)
        user-attribute (if (> (count user-name) 0)
                         { :username (first user-name) })
        classify-tag (if (= (count user-name) 0)
                       (map #(make-xml-node :classify
                                            {:id (str "Classify" %)
                                             :classifierName classifier
                                             :textId (str "Text" %)
                                             })
                            (range (count texts)))
                       (map #(make-xml-node :classify
                                            {:id (str "Classify" %)
                                             :classifierName classifier
                                             :textId (str "Text" %)
                                             :username (first user-name)})
                            (range (count texts))))
        read-calls (make-xml-node :readCalls
                                  {:readApiKey (keys :read-key)}
                                  classify-tag)
        uclassify-response (raw-post-request
                            (xml/emit-str
                             (xml-append-elements uclassify
                                                  (list texts-tag read-calls))))]
    (if (check-response
         (get-response uclassify-response))
      (readable-list
       (list
        (x/xml-> uclassify-response
                 :readCalls :classify
                 :classification (x/attr :textCoverage))
        (map vector
             (x/xml-> uclassify-response
                      :readCalls :classify
                      :classification :class
                      (x/attr :className))
             (x/xml-> uclassify-response
                      :readCalls :classify
                      :classification :class
                      (x/attr :p)))))
      false ;; Will never reach here
      )))

(defn classify-keywords
  "Sends a text to a classifier and returns a classification and
   relevant keywords for each class"
  [keys texts classifier & user-name]
  {:pre [(check-keys? keys)]}
  (let [textbase64-tag (map #(make-xml-node :textBase64
                                            {:id (str "Text" (str (index-of % texts)))}
                                            (String. (encode (.getBytes %)) )) texts)
        texts-tag (make-xml-node :texts {} textbase64-tag)
        user-attribute (if (> (count user-name) 0)
                         { :username (first user-name) })
        classify-tag (if (= (count user-name) 0)
                       (map #(make-xml-node :classifyKeywords
                                            {:id (str "Classify" %)
                                             :classifierName classifier
                                             :textId (str "Text" %)
                                             })
                            (range (count texts)))
                       (map #(make-xml-node :classifyKeywords
                                            {:id (str "Classify" %)
                                             :classifierName classifier
                                             :textId (str "Text" %)
                                             :username (first user-name)})
                            (range (count texts))))
        read-calls (make-xml-node :readCalls
                                  {:readApiKey (keys :read-key)}
                                  classify-tag)
        uclassify-response (raw-post-request
                            (xml/emit-str
                             (xml-append-elements uclassify
                                                  (list texts-tag read-calls))))]
    (if (check-response
         (get-response uclassify-response))
      (readable-list
       (list
        (x/xml-> uclassify-response
                 :readCalls :classifyKeywords
                 :classification (x/attr :textCoverage))
        (map vector
             (x/xml-> uclassify-response
                      :readCalls :classifyKeywords
                      :classification :class
                      (x/attr :className))
             (x/xml-> uclassify-response
                      :readCalls :classifyKeywords
                      :classification :class
                      (x/attr :p))
             (x/xml-> uclassify-response
                      :readCalls :classifyKeywords
                      :keywords :class x/text))))
      false                             ;Will never reach here
      )))
