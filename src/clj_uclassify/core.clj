(ns clj-uclassify.core
  (:require [http.async.client :as http]
            ))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn check-keys
  "Checks for key, if nil throws exception"
  [{rkey :read-key wkey :write-key}]
  (let [keys (list rkey wkey)]
    (if (some #(nil? %) keys)
      false
      true)))
      
(defn test-stuff [url]
  (with-open [client (http/create-client)] ; Create client
  (let [response (http/GET client "http://github.com/neotyk/http.async.client/")] ; request http resource
    (-> response
        http/await     ; wait for response to be received
        http/string))))   ; read body of response as string)
