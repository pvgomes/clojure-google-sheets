(ns clojure-google-sheets.core
  (:gen-class)
  (:require [clojure-google-sheets.config :as config]
            [clojure-google-sheets.sheets-v4 :as sheets-v4])
  (:import (com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp)
           (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets GoogleAuthorizationCodeFlow$Builder)
           (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
           (com.google.api.client.json.jackson2 JacksonFactory)
           (com.google.api.client.util.store DataStoreFactory)
           (com.google.api.services.sheets.v4 Sheets$Builder)))

(defn google-service
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
        receiver (sheets-v4/receiver port)]
    (-> (Sheets$Builder.
          http-transport json-factory
          (-> (AuthorizationCodeInstalledApp. flow receiver)
              (.authorize authorize)))
        (.setApplicationName application-name)
        .build)))

(defn -main
  [& _]
  (let [service (google-service {::application-name "Google Sheets API Java Quickstart"
                           ::access-type      "offline"
                           ::port             8888
                           ::authorize        "user"
                           ::credentials      (config/credentials)
                           ::tokens-directory (config/tokens)
                           ::scopes           config/scopes
                           ::json-factory     (JacksonFactory/getDefaultInstance)})
        ;values (sheets-v4/get-values service
        ;                   (:spreadsheet-id (config/sheet-config))
        ;                   "Sheet1!A2:E")
        values (sheets-v4/append service
                                     (:spreadsheet-id (config/sheet-config))
                                     "Sheet2!A2:E"
                                    ["Heitor em todo lugar"])

        ]
    (if (empty? values)
      (println "No data found on google sheets, check spreadsheet id and range.")
      (println "Stock, Current Price"))
    (doseq [[A B _C _D _E] values]
      (printf "%s, %s\n" A B))))