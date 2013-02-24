# clj-toml

A Clojure library to parse the [TOML](https://github.com/mojombo/toml) configuration format.

## Usage

    (ns your-namespace
      (:require [clj-toml.core :as toml]))

    ...

    (toml/parse my-string)

## TODO

* Support special characters in strings
* Support arrays
  - Multi-line
  - Nested
* Improved error reporting
* Parse from seqs of lines and from IO objects
* Unit tests (I'm bad)

## License

Copyright © 2013 Mark A. Nicolosi

Distributed under the Eclipse Public License, the same as Clojure.
