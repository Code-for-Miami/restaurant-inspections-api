(ns restaurant-inspections-api.schemas
  (:require [schema.core :as s]))

;; "district": "D1",
;; "countynumber": 23,
;; "countyname": "Dade",
;; "inspections": 20630
(s/defschema County {:district s/Str,
                     :countynumber s/Int
                     :countyname s/Str
                     :inspections s/Int})

;; "id": 1,
;; "description": "Approved source",
;; "is_risk_factor": true,
;; "is_primary_concern": false
(s/defschema Violation
  {:id s/Int
   :description s/Str
   :is_risk_factor Boolean
   :is_primary_concern Boolean})

;; "county_number": 11,
;; "location_address": "2500 NE 39 AVE",
;; "noncritical_violations_before_2013": null,
;; "license_type_code": "2015",
;; "location_city": "GAINESVILLE",
;; "business_name": "DOUBLE ENVELOP 71099",
;; "license_number": 1100012,
;; "location_zipcode": "32609",
;; "critical_violations_before_2013": null,
;; "location_latitude": null,
;; "location_longitude": null
(s/defschema Business
  {:county_number s/Int
   :location_address s/Str
   :noncritical_violations_before_2013 s/Int
   :license_type_code s/Str
   :location-city s/Str
   :business_name s/Str
   :license_number Long
   :location_zipcode s/Str
   :critical_violations_before_2013 s/Int
   :location_latitude s/Num
   :location_longitude s/Num})

;; "intermediateViolations": null,
;; "locationCity": "MARCO ISLAND",
;; "locationAddress": "250 S BEACH DR",
;; "countyName": "Collier",
;; "licenseTypeCode": "2010",
;; "businessName": "HIDEAWAY BCH THE POOL BAR",
;; "locationZipcode": "34145",
;; "totalViolations": 0,
;; "type": "inspections",
;; "inspectionDisposition": "Call Back - Complied",
;; "inspectionNumber": 2158023,
;; "basicViolations": null,
;; "id": "4598476",
;; "countyNumber": 21,
;; "licenseNumber": 2101212,
;; "visitNumber": 2,
;; "inspectionType": "Routine - Food",
;; "inspectionDate": "2016-06-15",
;; "highPriorityViolations": null,
;; "district": "D7"
(s/defschema Inspection
  {:intermediateViolations s/Int,
   :locationCity s/Str,
   :locationAddress s/Str,
   :countyName s/Str,
   :licenseTypeCode s/Str,
   :businessName s/Str,
   :locationZipcode s/Str,
   :totalViolations s/Int,
   :type s/Str,
   :inspectionDisposition s/Str,
   :inspectionNumber Long,
   :basicViolations s/Int,
   :id s/Str,
   :countyNumber s/Int,
   :licenseNumber Long,
   :visitNumber s/Int,
   :inspectionType s/Str,
   :inspectionDate java.util.Date,
   :highPriorityViolations s/Int,
   :district s/Str})

;; Has a little bit more info than normal inspection used in listings
(s/defschema InspectionDetail
  {:intermediateViolations s/Int,
   :locationCity s/Str,
   :locationAddress s/Str,
   :countyName s/Str,
   :licenseTypeCode s/Str,
   :businessName s/Str,
   :locationZipcode s/Str,
   :totalViolations s/Int,
   :type s/Str,
   :inspectionDisposition s/Str,
   :inspectionNumber Long,
   :basicViolations s/Int,
   :nonCriticalViolationsBefore2013 s/Int,
   :id s/Str,
   :countyNumber s/Int,
   :licenseNumber Long,
   :pdaStatus Boolean
   :visitNumber s/Int,
   :inspectionType s/Str,
   :inspectionDate java.util.Date,
   :violations [Violation]
   :highPriorityViolations s/Int,
   :criticalViolationsBefore2013 s/Int,
   :district s/Str})

(s/defschema UnprocessableError
  {:code 1
   :title "Validation Error"
   :detail "Invalid format or value for parameter."
   :source {:parameter s/Str}})

(defn wrap-data
  ""
  [data]
  {:meta []
   :data [data]})

(def swagger
  {:swagger
   {:ui "/api-docs"
    :spec "/swagger.json"
    :data {:info {:title "Florida Restaurant Inspections API",
                  :version "0.3.0",
                  :description "Florida RESTAPI",
                  ;; :termsOfService "",
                  :contact {:name "Joel Quiles",
                            :email "quilesbaker@gmail.com",
                            :url "//codefor.miami"},
                  :license {:name "MIT License",
                            :url "https://choosealicense.com/licenses/mit/"}},
           :produces ["application/json"],
           :consumes ["application/json"],
           :tags [], ;; TODO: if we want to add tags, we need to tag things below as well.

           :basePath ""

           :paths {"/counties"  {:get {:responses {200 {:schema County
                                                        :description "Counties description here..."}}
                                       :summary "Retrieve all available counties."}}

                   "/inspections" {:get {:responses {200 {:schema Inspection
                                                          :description "Returns all Inspections, 20 per page by default."}
                                                     422 {:schema UnprocessableError
                                                          :description "Unprocessable Query Parameters provided to inspections endpoint."}}
                                         :parameters {:query {(s/optional-key :countyNumber) s/Int
                                                              (s/optional-key :districtCode) s/Int
                                                              (s/optional-key :zipCodes) s/Str
                                                              (s/optional-key :startDate) s/Str
                                                              (s/optional-key :endDate) s/Str
                                                              (s/optional-key :businessName) s/Str}}
                                         :summary "Retrieve all Inspections; paginated."}}

                   "/inspections/:id" {:get {:responses {200 {:schema InspectionDetail
                                                              :description "Found it!"}}
                                             :parameters {:path {:id Long}}
                                             :summary "Retrieves one inspection by Id; includes linked violation resource details."}}

                   "/businesses" {:get {:responses {200 {:schema Business
                                                         :description "Found it!"}
                                                    422 {:schema UnprocessableError
                                                         :description "Unprocessable Query Parameters provided to businesses endpoint."}}
                                        :summary "adds two numbers together"}}

                   "/businesses/:licenseNumber" {:get {:responses {200 {:schema Business
                                                                        :description "Found it!"}}
                                                       :parameters {:path {:licenseNumber Long}}
                                                       :summary "adds two numbers together"}}

                   "/violations" {:get {:responses {200 {:schema Violation
                                                         :description "Found it!"}}
                                        :summary "adds two numbers together"}}}}}})
