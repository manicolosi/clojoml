(ns clojoml.core
  (:require [clojure.string :as str])
  (:import [java.text SimpleDateFormat]))

(defn- parse-group [group-name]
  (str/split group-name #"\."))

(def ^:private date-format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss"))

(defn- unescape [s]
  (-> s
    (str/replace #"\\0" "\000")
    (str/replace #"\\t" "\t")
    (str/replace #"\\n" "\n")
    (str/replace #"\\r" "\r")
    (str/replace #"\\\"" "\"")
    (str/replace #"\\\\" "\\")))

(defn- parse-value [v]
  (let [[_ array] (re-find #"\[(.*)\]" v)
        [_ string] (re-find #"^\"(.*)\"$" v)
        decimal (re-find #"^-?\d+\.\d+$" v)
        integer (re-find #"^-?\d+$" v)
        [_ bool] (re-find #"^(true|false)$" v)
        [_ date] (re-find #"^(.*)Z" v)]
    (cond
      array (vec (map (comp parse-value str/trim first)
                      (re-seq #"(\[.*,.*\]|\".*\"|[^, \[\]]+)" array)))
      string (unescape string)
      decimal (Double/parseDouble decimal)
      integer (Integer/parseInt integer)
      bool (Boolean/valueOf bool)
      date (.parse date-format date)
      :else (str "Unknown: '" v "'"))))
      ;:else (throw (RuntimeException. (str "Can't parse value: '" v "'"))))))

(defn- parse-kv [m group line]
  (let [[_ k v] (re-find #"^([^\s]+)\s*=(.*)$" (str/trim line))]
    (assoc-in m (conj group (keyword k)) (parse-value (str/trim v)))))

(defn- parse-lines [lines group acc]
  (if (nil? lines)
    acc
    (let [line (first lines)]
      (if-let [[_ group-name] (re-find #"^\s*\[(.*)\]" line)]
        (recur (next lines) (parse-group group-name) acc)
        (recur (next lines) group (parse-kv acc group line))))))

(defn- filter-lines [lines]
  (remove #(re-find #"^(\s*#.*|\s*)$" %1) lines))

(defn- parse-lines-helper [lines]
  (parse-lines (filter-lines lines) [] {}))

(defn parse-str [s]
  (parse-lines-helper (str/split-lines s)))

(defn parse-reader [reader]
  (parse-lines-helper (line-seq reader)))

(defn parse [obj]
  (condp #(isa? (class %2) %1) obj
    java.lang.String (parse-str obj)
    java.lang.Readable (parse-reader obj)
    (throw (RuntimeException. (str "Don't know how to parse '" (class obj) "'")))))
