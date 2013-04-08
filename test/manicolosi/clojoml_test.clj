(ns manicolosi.clojoml-test
  (:refer-clojure :exclude [booleans])
  (:use clojure.test
        manicolosi.clojoml))

(deftest strings
  (testing "parses plain strings"
    (is (= "value"
           (:key (parse "key = \"value\"")))))
  (testing "parses strings w/ special escapes"
    (is (= "\b"
           (:key (parse "key = \"\\b\""))))
    (is (= "\t"
           (:key (parse "key = \"\\t\""))))
    (is (= "\n"
           (:key (parse "key = \"\\n\""))))
    (is (= "\f"
           (:key (parse "key = \"\\f\""))))
    (is (= "\r"
           (:key (parse "key = \"\\r\""))))
    (is (= "\""
           (:key (parse "key = \"\\\"\""))))
    (is (= "/"
           (:key (parse "key = \"\\/\""))))
    (is (= "\\"
           (:key (parse "key = \"\\\\\"")))))
  (testing "parses strings w/ unicode escapes"
    (is (= "✓"
           (:key (parse "key = \"\\u2713\""))))))

(deftest numbers
  (testing "parses integers"
    (is (= 123 (-> "key = 123" parse :key)))
    (is (= -123 (-> "key = -123" parse :key))))
  (testing "parses floats"
    (is (= 1.23 (-> "key = 1.23" parse :key)))
    (is (= -1.23 (-> "key = -1.23" parse :key)))))

(deftest arrays
  (testing "parses single line arrays"
    (is (= [1 2 3] (-> "a = [1, 2, 3]" parse :a))))
  (testing "whitespace around commas doesn't matter"
    (is (= [1 2 3] (-> "a = [1,2,  3]" parse :a))))
  (testing "multiline works fine"
    (is (= [1 2 3] (-> "a = [1, 2,\n3]" parse :a))))
  (testing "non-homogenous is okay" ; This differs from the TOML spec
    (is (= [1 1.2 true] (-> "a = [1, 1.2, true]" parse :a)))))

(deftest comments
  (testing "full line comments are ignored"
    (is (= {} (-> "# Ignored" parse))))
  (testing "partial line comments are ignored"
    (is (= {:a 5} (-> "a = 5 # Ignored" parse)))))

(deftest booleans
  (testing "true and false are booleans"
    (is (= true (-> "key = true" parse :key)))
    (is (= false (-> "key = false" parse :key)))))

(deftest date-times
  (testing "parses ISO8601 zulu formatted time"
    (is (= #inst "1979-05-27T12:32:00.000-00:00"
           (-> "key = 1979-05-27T07:32:00Z" parse :key)))))

(deftest key-value-pairs
  (testing "parses key/values"
    (is (= {:key "value"}
           (parse "key = \"value\""))))
  (testing "ignores leading whitespace"
    (is (= {:key "value"}
           (parse "  key = \"value\""))))
  (testing "ignores trailing whitespace"
    (is (= {:key "value"}
           (parse "  key = \"value\"   "))))
  (testing "ignores whitespace around ="
    (is (= {:key "value"}
           (parse "  key  =\"value\"   ")))))

(deftest key-groups
  (testing "key groups and no key/values don't produce anything"
    (is (= {} (parse "[deeply.nested.hash]"))))
  (testing "key groups turn into nested hashes"
    (is (= {:deeply {:nested {:hash {:key true}}}}
           (parse "[deeply.nested.hash]\nkey = true"))))
  (testing "key/value pairs are nested within the last key group"
    (is (= {:one {:a 1} :two {:b 2}}
           (parse "[one]
                   a = 1
                   [two]
                   b = 2")))
    (is (= {:one {:a 1}}
           (parse "[one]\na = 1")))))
