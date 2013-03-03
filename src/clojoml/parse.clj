(ns clojoml.parse
  (:refer-clojure :exclude [char comment])
  (:use [the.parsatron])
  (:import [java.text SimpleDateFormat]))

(defn not-char [c]
  (token #(not= c %)))

(defn not-chars [& cs]
  (token (fn [c] (reduce #(and %1 %2) (map #(not= %1 c) cs)))))

(defparser eol []
  (let->> [_ (either (string "\n") (string "\r\n"))]
    (always nil)))

(defparser whitespace []
  (many (either (char \tab) (char \space))))

(defparser key-name []
  (let->> [k (many1 (not-chars \[ \] \= \tab \space \newline))]
    (->> k
         (apply str)
         always)))

(defparser comment-text []
  (let->> [c (many (not-char \newline))]
    (->> c
         (apply str)
         always)))

(defparser hex-digit []
  (either
    (digit)
    (token #{\a \b \c \d \e \f \A \B \C \D \E \F})))

(defn parse-hex [s]
  (Integer/valueOf s 16))

(defparser special-character []
  (let->> [_ (char \\)
           c (either
               (token #{\t \n \r \\ \"})
               (>> (char \x) (times 2 (hex-digit))))]
    (always
      (cond
        (= c \n) \newline
        (= c \t) \tab
        (= c \r) \return
        (seq? c) (->> c (apply str) parse-hex clojure.core/char)
        :else c))))

(defparser toml-string []
  (let->> [string (between (char \") (char \")
                           (many
                             (either
                               (special-character)
                               (not-char \"))))]
    (always (apply str string))))

(defn maybe [p]
  (either p (always [])))

(defn join
  ([p]
   (let->> [p p]
     (always (list p))))
  ([p & qs]
   (let->> [first p
            rest (apply join qs)]
     (always (conj rest first)))))

(defparser toml-number []
  (let->> [number (join
                    (maybe (char \-))
                    (many1 (digit))
                    (maybe (join (char \.)
                                 (many1 (digit)))))]
    (->> number
         flatten
         (apply str)
         read-string
         always)))

(defparser toml-bool []
  (let->> [bool (either
                  (string "true")
                  (string "false"))]
    (->> bool
         read-string
         always)))

(declare toml-value)

(defparser toml-array []
  (between (char \[) (char \])
           (let->> [_ (maybe (eol))
                    v1 (toml-value)
                    vrest (many (>> (char \,) (maybe (eol)) (toml-value)))
                    _ (whitespace)]
             (always (vec (conj vrest v1))))))

(def ^:private date-format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss"))

(defparser toml-datetime []
  ; Unsure why attempt is necessary... aparently toml-datetime is somehow
  ; consuming input when it fails...
  (attempt
    (let->> [datetime (join
                        (times 4 (digit))
                        (char \-)
                        (times 2 (digit))
                        (char \-)
                        (times 2 (digit))
                        (char \T)
                        (times 2 (digit))
                        (char \:)
                        (times 2 (digit))
                        (char \:)
                        (times 2 (digit))
                        (char \Z))]
      (->> datetime
           flatten
           (apply str)
           (.parse date-format)
           always))))

(defparser toml-value []
  (>> (whitespace)
      (choice
        (toml-array)
        (toml-string)
        (toml-datetime)
        (toml-number)
        (toml-bool))))

(defparser comment []
  (let->> [_ (char \#)
           _ (comment-text)
           _ (either (eol) (eof))]
    (always nil)))

(defparser key-value []
  (let->> [k (key-name)
           _ (between (whitespace) (whitespace) (char \=))
           v (toml-value)
           l (lineno)]
    (always {:type :key-value :line l :key k :val v})))

(defparser key-group []
  (let->> [g (between (char \[) (char \])
                      (key-name))
           l (lineno)]
    (always {:type :key-group :line l :key g})))

(defparser toml []
  (let->> [lines (many
                   (>> (whitespace)
                       (choice
                         (comment)
                         (eol)
                         (let->> [line (either (key-group) (key-value))
                                  _ (whitespace)
                                  _ (choice (comment) (eol) (eof))]
                           (always line)))))
           _ (eof)]
    (always lines)))
