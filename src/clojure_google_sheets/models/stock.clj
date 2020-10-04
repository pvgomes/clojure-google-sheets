(ns clojure-google-sheets.models.stock
  (:require
    [schema.core :as s]))

(def skeleton
  {:stock/id   {:schema s/Str
                 :eg "CYRE3"
                 :doc "the stock id"}
   :stock/buy-date {:schema s/Str
                 :eg "09/29/2020"
                 :doc "the date of buy"}
   :stock/quantity {:schema s/Int
                 :eg    100
                 :doc   "quantity of stocks"}
   :stock/price {:schema s/Num
                    :eg    2.56
                    :doc   "price on buy"}})

(s/defschema Stock skeleton)

;theres another columns but it could be calculated live
;amount = quantity * price
;current price
;profit
;profit %