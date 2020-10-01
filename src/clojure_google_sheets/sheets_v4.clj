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

(defn safe-to-double?
  [n]
  (= (bigdec n) (bigdec (double n))))

(defprotocol CellDataValue
  (->cell-data ^CellData [_]))

(extend-protocol CellDataValue
  Number
  (->cell-data [n]
    (when-not (safe-to-double? n)
      (throw (ex-info "Number value exceeds double precision" {:n n})))
    (-> (CellData.)
        (.setUserEnteredValue
          (-> (ExtendedValue.)
              (.setNumberValue (double n))))))
  String
  (->cell-data [s]
    (-> (CellData.)
        (.setUserEnteredValue
          (-> (ExtendedValue.)
              (.setStringValue s)))))
  clojure.lang.Keyword
  (->cell-data [kw]
    (->cell-data (str kw)))
  CellData
  (->cell-data [cd]
    cd)
  nil
  (->cell-data [_]
    (CellData.)))

(defn coerce-to-cell
  "Numbers and strings and keywords and date-times auto-coerce to CellData"
  [x]
  (->cell-data x))

(defn cell->clj
  "Converts cell data with either a userEnteredValue (x)or effectiveValue to a clojure type.
  stringValue -> string
  numberValue -> double
  DATE -> date-time
  else ~ identity"
  [cell-data]
  (let [ev (get cell-data "effectiveValue")
        uev (get cell-data "userEnteredValue")
        v (or ev uev)
        string-val (get v "stringValue")
        number-val (get v "numberValue")
        number-format (get-in cell-data ["userEnteredFormat" "numberFormat" "type"])
        date? (and (= "DATE" number-format) (some? number-val))
        currency? (and (= "CURRENCY" number-format) (some? number-val))
        empty-cell? (and (nil? ev) (nil? uev) (instance? CellData cell-data))]
    (when (and (some? ev)
               (some? uev))
      (throw (ex-info "Ambiguous cell data, contains both string effectiveValue and userEnteredValue"
                      {:cell-data cell-data})))
    (when (and (some? string-val)
               (some? number-val))
      (throw (ex-info "Ambiguous cell data value, contains both stringValue and numberValue"
                      {:cell-data cell-data})))
    (cond
      string-val
      string-val

      date?
      ;; https://developers.google.com/sheets/api/guides/concepts#datetime_serial_numbers
      (time/plus (time/date-time 1899 12 30) (time/days (long number-val)))

      currency?
      (bigdec number-val)

      number-val
      number-val

      empty-cell?
      nil

      :else
      cell-data)))

(defn row->row-data
  "google-ifies a row (list of columns) of type string?, number? keyword? or CellData."
  [row]
  (-> (RowData.)
      (.setValues (map coerce-to-cell row))))

(defn write-sheet
  "Overwrites the given sheet with the given rows of data. The data on the given
   sheet will be deleted and it will be resized to fit the given data exactly.
   This will be batched into requests of approximately 10k cell values. Larger
   requests yielded errors, though there is apparently no explicit limit or
   guidance given."
  ([service spreadsheet-id sheet-id rows]
   (write-sheet service spreadsheet-id sheet-id rows {}))
  ([^Sheets service spreadsheet-id sheet-id rows options]
   (assert (not-empty rows) "Must write at least one row to the sheet")
   (let [{:keys [batch-size]} (merge default-write-sheet-options options)
         sheet-id (int sheet-id)
         num-cols (int (apply max (map count rows)))
         first-row (first rows)
         part-size (long (/ batch-size num-cols))
         rest-batches (partition-all part-size (rest rows))
         first-batch (concat
                       [(-> (Request.)
                            (.setUpdateSheetProperties
                              (-> (UpdateSheetPropertiesRequest.)
                                  (.setFields "gridProperties")
                                  (.setProperties
                                    (-> (SheetProperties.)
                                        (.setSheetId sheet-id)
                                        (.setGridProperties
                                          (-> (GridProperties.)
                                              (.setRowCount (int (count rows)))
                                              (.setColumnCount (int num-cols)))))))))]
                       (when (< 0 (count rows))
                         [(-> (Request.)
                              (.setUpdateCells
                                (-> (UpdateCellsRequest.)
                                    (.setStart
                                      (-> (GridCoordinate.)
                                          (.setSheetId sheet-id)
                                          (.setRowIndex (int 0))
                                          (.setColumnIndex (int 0))))
                                    (.setRows [(row->row-data first-row)])
                                    (.setFields "userEnteredValue,userEnteredFormat"))))])
                       (when (< 1 (count rows))
                         [(-> (Request.)
                              (.setUpdateCells
                                (-> (UpdateCellsRequest.)
                                    (.setStart
                                      (-> (GridCoordinate.)
                                          (.setSheetId sheet-id)
                                          (.setRowIndex (int 1))
                                          (.setColumnIndex (int 0))))
                                    (.setRows (map row->row-data (first rest-batches)))
                                    (.setFields "userEnteredValue,userEnteredFormat"))))]))]
     (-> service
         (.spreadsheets)
         (.batchUpdate
           spreadsheet-id
           (-> (BatchUpdateSpreadsheetRequest.)
               (.setRequests first-batch)))
         (.execute))
     (loop [row-index (inc (count (first rest-batches)))
            batches (rest rest-batches)]
       (when (seq batches)
         (-> service
             (.spreadsheets)
             (.batchUpdate
               spreadsheet-id
               (-> (BatchUpdateSpreadsheetRequest.)
                   (.setRequests
                     [(-> (Request.)
                          (.setUpdateCells
                            (-> (UpdateCellsRequest.)
                                (.setStart
                                  (-> (GridCoordinate.)
                                      (.setSheetId sheet-id)
                                      (.setRowIndex (int row-index))
                                      (.setColumnIndex (int 0))))
                                (.setRows (map row->row-data (first batches)))
                                (.setFields "userEnteredValue,userEnteredFormat"))))])))
             (.execute))
         (recur (+ row-index (count (first batches)))
                (rest batches)))))))