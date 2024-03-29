# Globear-backend

A Clojure backend server based on ring, which serves content like 
geo-markers, pictures and thumbnails to the Globear-frontend application.

## Usage

The app is using `compojure` for routing and `lein-ring` plugin for HTTP request handling.
To spin up a development web server (using `:dev` profile) simply run 

```lein ring server```

The backend will then server request by default under port `3000`

All resources that are currently served by the backend (marker data, images, thumbnails etc) are loaded from the local file system. This will change in one  of the next Globear versions, but for now the `/resources` folder should contain `three` subdirectories:
* `/markers` - containing geo-markers in geojson format. You will find an example marker definition `geojson_marker.json` inside the directory to show the structure the geojson should have. Any new marker that is created via the Globear-frontend application will be added to marker definitions inside `geojson_marker.json` and will be written to disk. 
* `/pictures` - the images you want to attach to a marker defined in `geojson_marker.json`
* `/thumbnails` - the thumbnails corresponding to the images from /pictures. These are generated automatically at server start, so there is no need to insert anything manually here.

Available routes: \
* `/` - GET friendly greeting from the Globear-backend server :)
* `/markers/` - GET all geo-markers in geojson format
* `/pictures/:id` - GET picture with :id
* `/thumbnails/:id` - GET thumbnail with :id
* `/markers/` - PUT single geo-marker in edn-format
* `/pictures/` - PUT single picture (not yet implemented)

## TODO 
* feature: allow POST reqeusts with new picture content from Globear-frontend
* feature: cluster new picture uploads and generate new geo-makers from it
* feature: make gos-markers modifiable


## License
Copyright © 2019 Riccardo Valentini
