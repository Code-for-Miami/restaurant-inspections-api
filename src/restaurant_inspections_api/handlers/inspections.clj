(ns restaurant-inspections-api.handlers.inspections
  (:require [restaurant-inspections-api.validations :as validate]
            [restaurant-inspections-api.util :as util]
            [restaurant-inspections-api.services :as srv]
            [restaurant-inspections-api.db :as db]
            [restaurant-inspections-api.constants :as constants]
            [clojure.string :as str]))

(defn validate-inspections-params
  "Receives all inspections query parameters (nil when not specified) and returns a map of valid and
  invalid params."
  [{:keys [zipCodes businessName startDate endDate districtCode countyNumber perPage page]}]

  (validate/validate-params {:zipCodes (validate/zip-codes zipCodes)
                             :businessName (or businessName nil)
                             :startDate (validate/date (or startDate constants/earliest-date))
                             :endDate (or (validate/date endDate) (util/todays-date))
                             :districtCode (validate/district-code districtCode)
                             :countyNumber (validate/county-number countyNumber)
                             :perPage (or (validate/per-page perPage) 20)
                             :page (or (validate/page page) 0)}))

(defn format-params
  "Format all the pre-params sent to this endpoint"
  [params-map]
  (assoc params-map
         :businessName (when-let [businessName (:businessName params-map)]
                         (clojure.string/replace businessName #"\*" "%"))
         :zipCodes (when-let [zipCodes (:zipCodes params-map)]
                     (str/split zipCodes #","))
         :page (* (:page params-map) (:perPage params-map))))

(defn processable?
  [ctx]
  (validate/processable? validate-inspections-params ctx))

(defn handle-ok
  "Handles 200 OK for inspections"
  [{:keys [valid-params]}] ;; as ctx
  {:meta {:parameters valid-params}
   :data (into [] (map srv/format-data
                       (db/select-inspections-by-all (format-params valid-params))))})

(defn handle-unprocessable
  "Handles 422 Unprocessable when invalid params provided"
  [ctx]
  (merge {:meta
          {:parameters (get ctx :params)}}
         (get ctx :errors-map)))
