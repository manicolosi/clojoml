# Clojoml

A Clojure library to parse the [TOML](https://github.com/mojombo/toml)
configuration format. Clojoml is compliant with TOML 0.1.0 (although we allow
non-homogenous arrays to be consistent with Clojure's vectors and lists.

Clojoml is mostly compliant with [TOML
0.1.0](https://github.com/mojombo/toml/tree/v0.1.0) and is considered stable.
Clojoml does allow non-homogeneous arrays to be consistent with Clojure's
vectors and lists.

## Usage

```clojure
user=> (require '[manicolosi.clojoml :as clojoml])
nil

user=> (clojoml/parse "test = 123\n[group]\nsweet = \"action\"")
{"group" {"sweet" "action"}, "test" 123}

user=> (require '[clojure.java.io :as io])
nil

user=> (pprint (with-open [rdr (io/reader "example.toml")] (clojoml/parse rdr)))
{:clients {:data [["gamma" "delta"] [1 2]]},
 :servers
 {:beta {:dc "eqdc10", :ip "10.0.0.2"},
  :alpha {:dc "eqdc10", :ip "10.0.0.1"}},
 :database
 {:enabled true,
  :connection_max 5000,
  :ports [8001 8001 8002],
  :server "192.168.1.1"},
 :owner
 {:dob #inst "1979-05-27T12:32:00.000-00:00",
  :bio "GitHub Cofounder & CEO\nLikes tater tots and beer.",
  :organization "GitHub",
  :name "Tom Preston-Werner"},
 :title "TOML Example"}
```

## Installation

`clojoml` is available as a Maven artifact from [Clojars](https://clojars.org/clojoml):

``` clojure
:dependencies
  [[clojoml "0.1.0"]]
```

## TODO

* Test with [toml-test](https://github.com/BurntSushi/toml-test) (BurntSushi's
  language agnostic TOML test suite)

## Changes

### 0.1.0
* Clojoml's namespace moved from `clojoml.core` to `manicolosi.clojoml`
* Clojoml's version will match the version of the TOML spec that is supported
* Replaced hacky regex based parser with a proper parser using parsatron
* Implement changes to TOML strings (unicode and additional escapes)
* Complete test coverage of parser

## License

Copyright © 2013 Mark A. Nicolosi

Distributed under the Eclipse Public License, the same as Clojure.
