# vocab-frontend

A Clojure library designed to learn Chinese Vocabulary.

## Usage

### Useful commands

Update dependencies ```lein deps```  \
Compile ClojureScript application ```lein cljsbuild auto dev``` \
Run python server ```python -m SimpleHTTPServer 8080``` 

### TODO ###
* Use core.async to to server synchronization? (~ redux reducers)
* design appropriate data structure to hold vocabulary items:
    * character
    * Pinyin
    * translation
    * last seen
    * number of failures?
    * number of times seen

## License

Copyright © 2018 rival

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
