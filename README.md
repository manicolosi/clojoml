# Clojoml

A Clojure library to parse the [TOML](https://github.com/mojombo/toml)
configuration format. Check the TODO section below for what's currently
supported.

Compliant with revision 7cc56104bbf1a5b4119c2512bb34bb00be42bc86 of
specification. Main thing that is still missing is multi-line arrays.

## Usage

```clojure
user=> (require '[clojoml.core :as toml])
nil

user=> (toml/parse "test = 123\n[group]\nsweet = \"action\"")
{"group" {"sweet" "action"}, "test" 123}

user=> (require '[clojure.java.io :as io])
nil

user=> (pprint (with-open [rdr (io/reader "example.toml")] (toml/parse rdr)))
{"clients" {:data [["gamma\", \"delta" 1 2]]},
 "servers"
 {"beta" {:dc "eqdc10", :ip "10.0.0.2"},
  "alpha" {:dc "eqdc10", :ip "10.0.0.1"}},
 "database"
 {:enabled true,
  :connection_max 5000,
  :ports [8001 8001 8002],
  :server "192.168.1.1"},
 "owner"
 {:dob #inst "1979-05-27T12:32:00.000-00:00",
  :bio "GitHub Cofounder & CEO\nLikes tater tots and beer.",
  :organization "GitHub",
  :name "Tom Preston-Werner"},

```

## Installation

`clojoml` is available as a Maven artifact from [Clojars](https://clojars.org/clojoml):

    :dependencies
      [[clojoml "0.1.0"]]

## TODO

* Support multi-line arrays
* Improve arrays of strings
* Improved error reporting
* Unit tests (I'm bad)

## License

Copyright © 2013 Mark A. Nicolosi

Distributed under the Eclipse Public License, the same as Clojure.
