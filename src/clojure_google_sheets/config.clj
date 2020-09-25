(ns clojure-google-sheets.config
  (:require [clojure.java.io :as io])
  (:import (com.google.api.client.util.store FileDataStoreFactory)
           (com.google.api.services.sheets.v4 SheetsScopes)))

(defn credentials []
  (-> "credentials.json"
      io/resource
      io/reader))

(defn tokens []
  (-> "tokens"
      io/file
      FileDataStoreFactory.))

(def scopes
  [SheetsScopes/SPREADSHEETS_READONLY])