(ns clj-uclassify.core
  (:require [http.async.client :as http]
            [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.java.io :as io]
            [clojure.data.zip.xml :as x]
            ))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn- check-keys
  "Checks for key, if nil throw exception"
  [{rkey :read-key wkey :write-key}]
  (let [keys (list rkey wkey)]
    (if (some #(nil? %) keys)
      false
      true)))

(def akeys {:read-key "aD02ApbU29kNOG2xezDGXPEIck" :write-key "fsqAft7Hs29BgAc1AWeCIWdGnY"})
(akeys :read-key)

(defn make-xml-node
  ([node attrs] (xml/element node attrs))
  ([node attrs node-value] (xml/element node attrs node-value)))

(def ^:private uclassify
  (make-xml-node :uclassify
                 {:xmlns "http://api.uclassify.com/1/RequestSchema" :version "1.01"}))

(println (xml/emit-str uclassify))

(defn create [keys classifier]
  (if (check-keys keys)
    (xml/emit-str
     (zip/root
      (zip/append-child
       (zip/xml-zip uclassify)
       (make-xml-node :writeCalls {:writeApiKey (keys :write-key) :classifierName classifier}
                      (make-xml-node :create {:id "Create"})
                      ))))
    (throw (Throwable. "API key not found"))))

(create akeys "new_clj_csl")
  
(defn post-request [xml-data]
  (with-open [client (http/create-client)] ; Create client
  (let [response (http/POST client "http://api.uclassify.com" :body xml-data)] ; request http resource
    (-> response
        http/await     ; wait for response to be received
        http/string))))   ; read body of response as string)

(def b (post-request (create akeys "new clj_csii")))

(println b)

(defn- zip-str [s]
  "Workaround for converting xml-string to zipper"
  (zip/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))

(def t (zip-str b))

;; To do: write a function that returns the success of a call based on
;; the below tricks.

(x/xml-> t :status)
(x/xml-> t :status (x/attr :success)) ;Returns false or true
(x/xml-> t :status (x/attr :statusCode)) ;Returns status code like 4000
(x/xml-> t :status x/text) ;Returns error message or success message
