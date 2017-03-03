(ns restaurant-inspections-api.handlers.businesses
  (:require [restaurant-inspections-api.validations :as validate]
            [restaurant-inspections-api.util :as util]
            [restaurant-inspections-api.db :as db]
            [clojure.string :as str]))

(defn validate-businesses-params
  "Recieves all businesses query parameters (nil when not specified) and
  returns a map of valid and invalid params"
  [{:keys [zipCodes countyNumber perPage page]}]
  (validate/validate-params {:zipCodes (validate/zip-codes zipCodes)
                             :countyNumber (validate/county-number countyNumber)
                             :perPage (or (validate/per-page perPage) 20)
                             :page (or (validate/page page) 0)}))

(defn- format-businesses-params
  "Returns the parameters map with the zip codes as an array and page calculated"
  [params-map]
  (assoc params-map
         :zipCodes (when-let [zip-codes (:zipCodes params-map)]
                     (str/split  zip-codes #","))
         :page (* (Integer. (:perPage params-map)) (Integer. (:page params-map)))))

(defn processable?
  "Returns true and valid parameters or false and list of errors
   if the parameters are valid or not"
  [ctx]
  (validate/processable? validate-businesses-params ctx))

(defn handle-ok
  "Handles 200 OK for inspections"
  [{:keys [valid-params]}]
  {:meta {:parameters valid-params}
   :data (into [] (db/select-all-restaurants (format-businesses-params valid-params)))})

(defn handle-unprocessable
  "Handles 422 Unprocessable when invalid params provided"
  [ctx]
  (merge {:meta
          {:parameters (get ctx :params)}}
         (get ctx :errors-map)))