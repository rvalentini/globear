# Globear-frontend

A ClojureScript image viewer application based on Mapbox GL JS.
This front end 
which is intended as travel-blog/photo album/foodie-guide. 
Not sure yet what will come out in the end, but it surely will be 'beartastic' ;)

## Usage

The application is using `figwheel.main` for building and dependency management and serves the application by default under port `9500`. Without connection to the Globear-backend server, this will only display an empty map. The application will try to connect to the Globear-backend on startup and request the geo-marker definitions from `http://localhost:3000/geojson`. 

Clicking on a geo-marker will open a Popup and all additional resources attached to this geo-marker (thumbnails & images) will then be loaded from the backend server. 

Old version using `lein-figwheel` (deprecated!): \
~~Compile ClojureScript application ```lein cljsbuild auto dev```~~ \  
~~Run python server ```python -m SimpleHTTPServer 8080```~~ \
~~Compile & run ClojureScript application ```lein figwheel```~~ \

New version using figwheel.main: \
Run dev application server with REPL: ```clojure -m figwheel.main --build globear --repl ``` \
Close REPL again :) ```:cljs/quit```


## TODO 
* ~~Introduce Totoro-themed markers to the map~~
* ~~Use core.async for client server communication handling?~~
* ~~Combine reagent state and picture downloads via core.async -> how does the 
  map state update when picture download is finished?~~
* ~~Come up with a reasonable data structure for markers and contained pictures~~
* ~~Introduce cluster markers~~
* feature: integrate 'food-guide' functionality
* feature: set new geo-marker on the map and upload additional pictures / content


## License
Copyright © 2019 Riccardo Valentini


