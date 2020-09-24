(ns clojure-google-sheets.core
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:import (com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp)
           (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver$Builder)
           (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets GoogleAuthorizationCodeFlow$Builder)
           (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
           (com.google.api.client.json.jackson2 JacksonFactory)
           (com.google.api.client.util.store FileDataStoreFactory DataStoreFactory)
           (com.google.api.services.sheets.v4 Sheets$Builder SheetsScopes Sheets)
           (com.google.api.services.sheets.v4.model ValueRange)))

(set! *warn-on-reflection* true)

(defn gservice
  [{::keys [^String application-name ^DataStoreFactory tokens-directory
            credentials authorize json-factory scopes access-type port]}]
  (let [http-transport (GoogleNetHttpTransport/newTrustedTransport)
        client-secrets (GoogleClientSecrets/load json-factory credentials)
        flow (-> (GoogleAuthorizationCodeFlow$Builder.
                   http-transport
                   json-factory
                   client-secrets
                   scopes)
                 (.setDataStoreFactory tokens-directory)
                 (.setAccessType access-type)
                 .build)
        receiver (-> (LocalServerReceiver$Builder.)
                     (.setPort port)
                     .build)]
    (-> (Sheets$Builder.
          http-transport json-factory
          (-> (AuthorizationCodeInstalledApp. flow receiver)
              (.authorize authorize)))
        (.setApplicationName application-name)
        .build)))

(defn get-values
  [^Sheets gservice id range]
  (let [^ValueRange response (-> gservice
                                 .spreadsheets
                                 .values
                                 (.get id range)
                                 .execute)]
    (.getValues response)))

(defn -main
  [& _]
  (let [service (gservice {::application-name "Google Sheets API Java Quickstart"
                           ::access-type      "offline"
                           ::port             8888
                           ::authorize        "user"
                           ::credentials      (-> "credentials.json"
                                                  io/resource
                                                  io/reader)
                           ::tokens-directory (-> "tokens"
                                                  io/file
                                                  FileDataStoreFactory.)
                           ::scopes           [SheetsScopes/SPREADSHEETS_READONLY]
                           ::json-factory     (JacksonFactory/getDefaultInstance)})
        values (get-values service
                           "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
                           "Class Data!A2:E")]
    (if (empty? values)
      (println "No data found.")
      (println "name, major"))
    (doseq [[A _B _C _D E] values]
      (printf "%s, %s\n" A E))))