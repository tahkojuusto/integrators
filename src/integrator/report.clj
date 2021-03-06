(ns integrator.report
    (:require [clojure.data.json :as json])
    (:require [clojure.tools.logging :as log]))

(defn -stringify-JSON-report
    "Print the JSON representation of integration results."
    [data]
    (log/trace "ENTERING report/-stringify-JSON-report.")
    (json/pprint-json data))

(defn create-JSON-method-struct
    "Create a JSON structure representing the integration method
    and its result."
    [method-name result]
    (log/trace "ENTERING report/create-JSON-method-struct.")
    {:method-name method-name
     :result      result})

(defn create-JSON-config-struct
    "Create a JSON structure representing the configuration
    settings."
    [x0 y0 x1 y1 N]
    (log/trace "ENTERING report/create-JSON-config-struct.")
    {:x0          x0
     :y0          y0
     :x1          x1
     :y1          y1
     :sample-size N})

(defn create-JSON-report
    "Combine method and configuration JSON structures into one
    structure."
    [methods config]
    (log/trace "ENTERING report/create-JSON-report.")
    {:configuration {:sample-size (:sample-size config)
                     :x0          (:x0 config)
                     :y0          (:y0 config)
                     :x1          (:x1 config)
                     :y1          (:y1 config)}
     :methods methods})

(defn create-report
    "Create report for integration results."
    [methods config]
    (log/trace "ENTERING report/create-report.")
    (-stringify-JSON-report (create-JSON-report methods config)))