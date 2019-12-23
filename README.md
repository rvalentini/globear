# Globear

<p align="center">
  <img src="/globear-frontend/resources/public/bear.png" width="100" title="Globear">
</p>

A 'beartastic' image viewer application written in Clojure/ClojureScript using Mapbox GL JS and Reagent. 

I started to work on this application with the goal to have my own personalized, highly customized travel/blog/food-guide/photo-diary/stuff-my-parents-would-like-to-see application and also to learn more about programming in Clojure and Clojurescript. The application is not anywhere near complete at the moment, but already provides enough features to be used as a geospatial image viewer. I am of course working relentlessly on creating new features for Globear and you can see a rough outline of the things to come in the TODO section below. 

in this project I managed to combine three of my interests:
1. Programming Clojure
2. Traveling 
3. Taking some touristy snapshots with my mobile phone


In case you have any questions/advice/suggestions/feature-requests/rants, please feel free to contanct me. 

Travel the world and see all the things! 

<p align="center">
  <img src="/globear-frontend/resources/public/screenshot_popup.png" width="350" title="Popup Overlay View">
  <img src="/globear-frontend/resources/public/screenshot_marker_1.png" width="350" title="Map Marker View Zoom-in">
  <img src="/globear-frontend/resources/public/screenshot_marker_2.png" width="350" title="Map Marker View Zoom-out">

</p>

## Specs

The frontend is written in ClojureScript using Figwheel Main and Reagent and relies (unfortunately) heavly on JavaScript-interops in order to use the Mapbox GL JS API. 

The backend is written entirely in Clojure with a basic Leiningen setup, using Ring for HTTP request handling and Compojure for routing.


## Usage

For usage instructions of the application, see the README files in the respective `frontend` and `backend` module packages.

## Features

Current state of Globear now includes the following features:

* Awsome bear-themed geo-markers :D 
* Marker popups display thumbnail presentation of the pictures
* Full-screen picture overlay with lazy loading from backend
* Cluster-markers with bear-themed size indicators for lower zoom steps
* Automatic thumbnail generation in the backend on startup of the app
* New markers can be added via right-click on the map

## TODO

1. ~~[Frontend] load large picture on pop-up via actions~~
2. ~~[Frontend/Backend] make sure resources are cached properly~~
3. ~~[Frontend] Correct picture orientations (try to parse meta-data using exif-processor)~~
4. ~~[Frontend] introduce Cluster-Marker~~
5. ~~[Backend] Nicer Error handling for IO Exceptions~~
6. ~~[Frontend] Switch to new Globear icons~~
7. ~~[Mapbox] try out different map styles~~ 
8. [Backend] make AWS deployable
9. [Frontend] feature: upload pictures and set markers 
10. [Frontend] feature: new marker types -> restaurant / text / etc
11. [Frontend] feature: more context functionality for right-click on marker 

## Design

A big thanks for designing the globear icon goes to Kirikia. 
You can find more of her awsome works [here](https://www.instagram.com/kirikia/)

## License
Copyright © 2019 Riccardo Valentini