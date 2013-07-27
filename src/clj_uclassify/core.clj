(ns clj-uclassify.core
  (:require [http.async.client :as http]
            [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.java.io :as io]
            [clojure.data.zip.xml :as x]))

(defn check-keys
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

(def uclassify
  (make-xml-node :uclassify
                 {:xmlns "http://api.uclassify.com/1/RequestSchema" :version "1.01"}))

(defn check-response
  "Checks uClassifyResponse and returns true or throws an exception"
  [response]
  (if (= "true" (first (:success response)))
    true
    (throw (Throwable. (first (:status response))))))

(defrecord uClassifyResponse
    [success statusCode status])

(defn get-response [xml-zipper]
  "Returns uClassifyResponse containg the response status"
  (uClassifyResponse.
   (x/xml-> xml-zipper :status (x/attr :success))
   (x/xml-> xml-zipper :status (x/attr :statusCode))
   (x/xml-> xml-zipper :status x/text)))

(defn zip-str
  "Workaround for converting xml-string to zipper"
  [s]
  (zip/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))


(defn post-request [xml-data]
  (with-open [client (http/create-client)] ; Create client
  (let [response (http/POST client "http://api.uclassify.com" :body xml-data)] ; request http resource
    (-> response
        http/await     ; wait for response to be received
        http/string    ; read body of response as string
        zip-str
        get-response
        check-response))))
