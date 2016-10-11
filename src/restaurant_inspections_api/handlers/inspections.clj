(ns restaurant-inspections-api.handlers.inspections
  (:require [restaurant-inspections-api.validations :as validate]
            [restaurant-inspections-api.util :as util]
            [restaurant-inspections-api.services :as srv]
            ))

(defn validate-inspections-params
  "Receives all inspections query parameters (nil when not specified) and returns a map of valid and
  invalid params."
  [zip-codes business-name start-date end-date district-code county-number per-page page]

  (let [validated-map {:zipCodes (validate/zip-codes zip-codes)
                       :businessName business-name
                       :startDate (validate/date start-date)
                       :endDate (validate/date end-date)
                       :districtCode (validate/district-code district-code)
                       :countyNumber (validate/county-number county-number)
                       :perPage (validate/per-page per-page)
                       :page (validate/page page)}]

    {:invalid (into {} (filter #(false? (second %)) validated-map))
     :valid (into {} (filter #(boolean (second %)) validated-map))}))

(defn format-query-params-error
  "Receives param-key for an error and returns a map with error details."
  [param-key]
  {:code 1
   :title "Validation Error"
   :detail "Invalid format or value for parameter."
   :source {:parameter param-key}})

(defn processable?
  "Given a ring server context, returns true or false if parameters are valid/processable. Also sets result (errors, correct fields) into the context."
  [ctx]
  ; lets make a map of field names, values, and valid
  (let [zip-codes (get-in ctx [:request :params :zipCodes])
        business-name (get-in ctx [:request :params :businessName])
        start-date (or (get-in ctx [:request :params :startDate]) "2013-01-01")
        end-date (or (get-in ctx [:request :params :endDate]) (util/todays-date))
        district-code (get-in ctx [:request :params :district])
        county-number (get-in ctx [:request :params :countyNumber])
        per-page (or (get-in ctx [:request :params :perPage]) "20")
        page (or (get-in ctx [:request :params :page]) "1")
        validations-map (validate-inspections-params zip-codes business-name
                                                     start-date end-date
                                                     district-code
                                                     county-number
                                                     per-page
                                                     page)]
    (if-not (empty? (:invalid validations-map))
      [false {:errors-map
              {:errors  ;; for each invalid-params here
               (into [] (for [keyval (:invalid validations-map)]
                          (format-query-params-error (name (key keyval)))))}}]
      [true {:valid-params (:valid validations-map)}])))

(defn handle-ok
  ""
  [ctx]
  {:meta {:parameters (get ctx :valid-params)}
   :data (into [] (srv/inspections-by-all (get ctx :valid-params)))})

