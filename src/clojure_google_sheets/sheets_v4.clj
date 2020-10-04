(ns clojure-google-sheets.sheets-v4
  (:require
    [schema.core :as s])
  (:import (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver$Builder)
           (com.google.api.services.sheets.v4 Sheets)
           (com.google.api.services.sheets.v4.model AppendCellsRequest
                                                    ValueRange
                                                    BatchUpdateSpreadsheetRequest
                                                    CellData
                                                    ExtendedValue
                                                    GridCoordinate
                                                    Request
                                                    RowData
                                                    UpdateCellsRequest
                                                    BatchUpdateSpreadsheetResponse)))

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

(s/defn write-values :- BatchUpdateSpreadsheetResponse
  [^Sheets gservice id sheet-id row-index values]
  (let [write-request (-> (Request.)
                          (.setUpdateCells
                            (-> (UpdateCellsRequest.)
                                (.setStart
                                  (-> (GridCoordinate.)
                                      ;sheet id is a range on a sheet. All indexes are zero-based. it means that a first sheet is 0,
                                      ;Easiest way to find the sheet id is in the browser URL when you open the spreadsheet / sheet: https://docs.google.com/spreadsheets/d/{spreadsheetId}/edit#gid={sheetId}
                                      ;more info see here https://developers.google.com/resources/api-libraries/documentation/sheets/v4/csharp/latest/classGoogle_1_1Apis_1_1Sheets_1_1v4_1_1Data_1_1GridRange.html
                                      (.setSheetId (int sheet-id))
                                      ;1 = 0 (first row)
                                      (.setRowIndex (int row-index))
                                      ;A = 0 (first column)
                                      (.setColumnIndex (int 0))))
                                (.setRows [(row->row-data values)])
                                (.setFields "userEnteredValue,userEnteredFormat"))))]
                     (-> gservice
                         (.spreadsheets)
                         (.batchUpdate
                           id
                           (-> (BatchUpdateSpreadsheetRequest.)
                                  (.setRequests [write-request])))
                         .execute)))

(defn append-sheet
  "appends rows to a sheet (tab). Appends starting at the last
  non-blank row"
  [^Sheets service id sheet-id rows]
  (assert (not-empty rows) "Must write at least one row to the sheet")
  (let [sheet-id (int sheet-id)
        num-cols (int (count (first rows)))
        part-size (long (/ 10000 num-cols))
        batches (partition part-size part-size [] rows)
        first-batch [(-> (Request.)
                         (.setAppendCells (-> (AppendCellsRequest.)
                                              (.setSheetId sheet-id)
                                              (.setRows (map row->row-data (first batches)))
                                              (.setFields "userEnteredValue,userEnteredFormat"))))]]
    (doall (cons (-> service
                     (.spreadsheets)
                     (.batchUpdate id
                                   (-> (BatchUpdateSpreadsheetRequest.) (.setRequests first-batch)))
                     (.execute))
                 (map (fn [batch]
                        (-> service
                            (.spreadsheets)
                            (.batchUpdate
                              id
                              (-> (BatchUpdateSpreadsheetRequest.)
                                  (.setRequests [(-> (Request.)
                                                     (.setAppendCells
                                                       (-> (AppendCellsRequest.)
                                                           (.setSheetId sheet-id)
                                                           (.setRows (map row->row-data batch))
                                                           (.setFields "userEnteredValue,userEnteredFormat"))))])))
                            (.execute)))
                      (rest batches))))))