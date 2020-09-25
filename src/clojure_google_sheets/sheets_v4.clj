(ns clojure-google-sheets.sheets-v4
  (:import (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver$Builder)
           (com.google.api.services.sheets.v4 Sheets)
           (com.google.api.services.sheets.v4.model ValueRange)))

(set! *warn-on-reflection* true)

(defn receiver [port]
  (-> (LocalServerReceiver$Builder.)
      (.setPort port)
      .build))

(defn get-values
  [^Sheets gservice id range]
  (let [^ValueRange response (-> gservice
                                 .spreadsheets
                                 .values
                                 (.get id range)
                                 .execute)]
    (.getValues response)))