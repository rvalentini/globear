# globear

A Clojure map application based on mapbox.gl, 
which is intended as travel-blog/photo album/foodie-guide. 
Not sure yet what will come out in the end, but it surely will be 'beartastic' ;)

## Usage

### Useful commands

Update dependencies ```lein deps```  \
~~Compile ClojureScript application ```lein cljsbuild auto dev```~~   
~~Run python server ```python -m SimpleHTTPServer 8080```~~ \
Compile & run ClojureScript application ```lein figwheel```

### TODO ###
* Use core.async to to server synchronization? (~ redux reducers)
* come up with a reasonable data structure for markers and contained pictures
* integrate 'foodie-guide' functionality
* create marker reducers depending on the zoom-level (cluster markers)

## License

Copyright © 2019 rival


