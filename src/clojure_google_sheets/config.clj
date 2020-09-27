(ns clojure-google-sheets.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (com.google.api.client.util.store FileDataStoreFactory)
           (com.google.api.services.sheets.v4 SheetsScopes)))

(defn sheet-config
  ([] (-> "template.config.edn"
          io/resource
          io/reader
          java.io.PushbackReader.
          edn/read))
  ([config-file] (-> config-file
                     io/resource
                     io/reader
                     java.io.PushbackReader.
                     edn/read)))

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