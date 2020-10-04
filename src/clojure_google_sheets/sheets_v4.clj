(ns clojure-google-sheets.sheets-v4
  (:require [clj-time.core :as time])
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
                                                    UpdateSheetPropertiesRequest)))

(set! *warn-on-reflection* true)

(def default-write-sheet-options
  {:batch-size 10000})

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

(defn make-update-cells [start rows fields]
  (-> (Request.)
      (.setUpdateCells (-> (UpdateCellsRequest.)
                           (.setStart start)
                           (.setRows rows)
                           (.setFields fields)))))

(defn write-values
  [^Sheets gservice id range values]
  (let [write-request (-> (Request.)
                          (.setUpdateCells
                            (-> (UpdateCellsRequest.)
                                (.setStart
                                  (-> (GridCoordinate.)
                                      (.setSheetId (int 12))
                                      (.setRowIndex (int 0))
                                      (.setColumnIndex (int 0))))
                                (.setRows {})
                                (.setFields "userEnteredValue,userEnteredFormat"))))]

                     (-> gservice
                         (.spreadsheets)
                         (.batchUpdate
                           id
                           (-> (BatchUpdateSpreadsheetRequest.)
                               (.setRequests write-request)))))
  )

(defn vai []
  (-> (Request.)
      (.setUpdateCells
        (-> (UpdateCellsRequest.)
            (.setStart
              (-> (GridCoordinate.)
                  (.setSheetId sheet-id)
                  (.setRowIndex (int 0))
                  (.setColumnIndex (int 0))))
            (.setRows [(row->row-data first-row)])
            (.setFields "userEnteredValue,userEnteredFormat"))))

  (-> service
      (.spreadsheets)
      (.batchUpdate
        spreadsheet-id
        (-> (BatchUpdateSpreadsheetRequest.)
            (.setRequests first-batch)))
      (.execute))
  )

