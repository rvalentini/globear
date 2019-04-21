# globear

A Clojure map application based on mapbox.gl, 
which is intended as travel-blog/photo album/foodie-guide. 
Not sure yet what will come out in the end, but it surely will be 'beartastic' ;)

## Usage

### Useful commands

Update dependencies ```lein deps```  \
Old version using `lein-figwheel`: \
~~Compile ClojureScript application ```lein cljsbuild auto dev```~~ \  
~~Run python server ```python -m SimpleHTTPServer 8080```~~ \
~~Compile & run ClojureScript application ```lein figwheel```~~ \

New version using figwheel.main: \
Run application with REPL: ```clojure -m figwheel.main --build globear --repl ``` \
Close REPL again :) ```:cljs/quit```


### TODO ###
* Use core.async to to server synchronization? (~ redux reducers)
* Combine reagent state and picture downloads via core.async -> how does the 
  map detect when picture download is finished?
* come up with a reasonable data structure for markers and contained pictures
* integrate 'foodie-guide' functionality
* create marker reducers depending on the zoom-level (cluster markers)

## License

Copyright © 2019 rival


