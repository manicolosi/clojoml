# Clojoml

A Clojure library to parse the [TOML](https://github.com/mojombo/toml)
configuration format. Check the TODO section below for what's currently
supported.

## Usage

```clojure
user=> (require '[clojoml.core :as toml])
nil

user=> (toml/parse "test = 123\n[group]\nsweet = \"action\"")
{"group" {"sweet" "action"}, "test" 123}

user=> (require '[clojure.java.io :as io])
nil

user=> (pprint (with-reader [rdr (io/reader "example.toml")] (toml/parse rdr)))
{"servers"
 {"beta" {"dc" "eqdc10", "ip" "10.0.0.2"},
  "alpha" {"dc" "eqdc10", "ip" "10.0.0.1"}},
 "database"
 {"enabled" true, "connection_max" 5000, "server" "192.168.1.1"},
 "owner"
 {"dob" #inst "1979-05-27T12:32:00.000-00:00",
  "bio" "GitHub Cofounder & CEO\\nLikes tater tots and beer.",
  "organization" "GitHub",
  "name" "Tom Preston-Werner"},
 "title" "TOML Example"}
```

## TODO

* Support special characters in strings
* Support arrays
  - Multi-line
  - Nested
* Improved error reporting
* Parse from seqs of lines and from IO objects
* Unit tests (I'm bad)
* Partial line comments

## License

Copyright © 2013 Mark A. Nicolosi

Distributed under the Eclipse Public License, the same as Clojure.
