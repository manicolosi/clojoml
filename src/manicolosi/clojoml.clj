(ns manicolosi.clojoml
  (:refer-clojure :exclude [comment])
  (:require [clojure.string :as str])
  (:use [manicolosi.clojoml.parser]))

(defn- transform [lines group acc]
  (if (empty? lines)
    acc
    (let [line (first lines)]
      (condp #(= %1 (:type %2)) line
        :key-value (recur (next lines)
                          group 
                          (assoc-in acc
                                    (conj group (keyword (:key line)))
                                    (:val line)))
        :key-group (recur (next lines)
                          ; Parser should split keys
                          (mapv keyword (str/split (:key line) #"\."))
                          acc)))))

(defprotocol TomlParsable
  (_parse [this]))

(extend-type java.lang.String
  TomlParsable
  (_parse [this]
    (run-toml this)))

(extend-type java.lang.Readable
  TomlParsable
  (_parse [this]
    (run-toml (slurp this))))

(defn parse [obj]
  (transform (_parse obj) [] {}))
