(ns clojure-google-sheets.sheets-v4
  (:require [clj-time.core :as time]
            [clojure-google-sheets.config :as config])
  (:import (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver$Builder)
           (com.google.api.services.sheets.v4 Sheets)
           (com.google.api.services.sheets.v4.model ValueRange
                                                    AddSheetRequest
                                                    AppendCellsRequest
                                                    BatchUpdateSpreadsheetRequest
                                                    CellData
                                                    CellFormat
                                                    DeleteDimensionRequest
                                                    DimensionRange
                                                    ExtendedValue
                                                    GridCoordinate
                                                    GridProperties
                                                    InsertDimensionRequest
                                                    NumberFormat
                                                    Request
                                                    RowData
                                                    SheetProperties
                                                    UpdateCellsRequest
                                                    UpdateSheetPropertiesRequest)
           (com.google.api.client.json.jackson2 JacksonFactory)
           (com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp)
           (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets GoogleAuthorizationCodeFlow$Builder)
           (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
           (com.google.api.client.json.jackson2 JacksonFactory)
           (com.google.api.client.util.store DataStoreFactory)
           (com.google.api.services.sheets.v4 Sheets$Builder)))

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

(defn row->row-data
  "google-ifies a row (list of columns) of type string?, number? keyword? or CellData."
  [row]
  (-> (RowData.)
      (.setValues (map #(-> (CellData.)
                            (.setUserEnteredValue
                              (-> (ExtendedValue.)
                                  (.setStringValue %)))) row))))

(defn write-values
  [^Sheets gservice id range values]
  (let [write-request (-> (Request.)
                          (.setUpdateCells
                            (-> (UpdateCellsRequest.)
                                (.setStart
                                  (-> (GridCoordinate.)
                                      (.setSheetId nil)
                                      (.setRowIndex (int 0))
                                      (.setColumnIndex (int 0))))
                                (.setRows [(row->row-data values)])
                                (.setFields "userEnteredValue,userEnteredFormat"))))]
                     (-> gservice
                         (.spreadsheets)
                         (.batchUpdate
                           id
                           (-> (BatchUpdateSpreadsheetRequest.)
                                  (.setRequests [write-request])))
                         .execute)
))

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
        receiver (receiver port)]
    (-> (Sheets$Builder.
          http-transport json-factory
          (-> (AuthorizationCodeInstalledApp. flow receiver)
              (.authorize authorize)))
        (.setApplicationName application-name)
        .build)))

(comment


  (let [service (google-service {::application-name "Google Sheets API Java Quickstart"
                                 ::access-type      "offline"
                                 ::port             8888
                                 ::authorize        "user"
                                 ::credentials      (config/credentials)
                                 ::tokens-directory (config/tokens)
                                 ::scopes           config/scopes
                                 ::json-factory     (JacksonFactory/getDefaultInstance)})
        ;values (get-values service
        ;                   (:spreadsheet-id (config/sheet-config))
        ;                   "Sheet1!A2:E")
        values (write-values service
                             (:spreadsheet-id (config/sheet-config))
                             "Sheet2!A2:E"
                             (java.util.ArrayList. ["Berlin", "Brussels", "Helsinki", "Madrid", "Oslo", "Paris","Stockholm"]))

        ]
    (println values))


  )




