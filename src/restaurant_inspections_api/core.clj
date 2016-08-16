(ns restaurant-inspections-api.core
  (:require [clojure.tools.logging :as log]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]
            [compojure.handler :refer [site]]
            ; internal
            [restaurant-inspections-api.environment :as env]
            [restaurant-inspections-api.cors :refer [all-cors]]
            [restaurant-inspections-api.routes :refer [all-routes]]
            [restaurant-inspections-api.cron.core :refer [load-api-data]])
  (:gen-class))

(def port (env/get-env-port))

(defn -main
  "Starts the server"
  [& args]
  (let [handler (if (not (env/in-prod?))
                  (do (log/info "Server in dev. mode, running hot-reload")
                      (reload/wrap-reload (all-cors (site #'all-routes))))
                  (do (log/info "Server in production mode")
                      (all-cors (site all-routes))))]
    (log/info "Running server on port " port)
    (load-api-data)
    (run-server handler {:port port})))
