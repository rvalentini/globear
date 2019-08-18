# globear

[insert small Totoro here]

A 'beartastic' image viewer application written in Clojure/ClojureScript using mapbox gl js. 

I started to work on this application with the goal to have my own personalized, highly customized travel/blog/food-guide/photo-diary/stuff-my-parents-would-like-to-see application and also to learn more about programming in Clojure and Clojurescript. The application is not anywhere near complete at the moment, but already provides enough features to be used as a geospatial iamge viewer. I am of course working relentlessly on creating new features for globear and you can see a rough outline of the things to come in the TODO section below. 

in this project I managed to combined three of my interests:
1. Clojure
2. Traveling 
3. The works of Hayao Miyazaki

In case you have any questions/advice/suggestions/feature-requests/rants or just want to talk about Hayao Miyazaki, please feel free to contanct me. 

Travel the world and see all the things! 

## Specs

The frontend is written in ClojureScript using Figwheel Main and Reagent and relies (unfortunately) heavly on JavaScript-interops in order to use the Mapbox GL JS API. 

The backend is written entirely in Clojure with a basic Leiningen setup, using Ring for HTTP request handling and Compojure for routing.


## Features

Current state of globear now includes the following features:

* Totoro-themed geo-markers :D
* Marker popups deisplay thumbnail presentation of the pictures
* Full-screen picture overlay with lazy loading from backend
* Cluster-markers with Totoro-themed size indicators for lower zoom steps
* Automatic thumbnail generation in the backend on startup

## TODO

1. ~~[Frontend] load large picture on pop-up via actions~~
2. ~~[Frontend/Backend] make sure resources are cached properly~~
3. ~~[Frontend] Correct picture orientations (try to parse meta-data using exif-processor)~~
4. ~~[Frontend] introduce Cluster-Marker~~
5. [Frontend/Backend] introduce basic auth
6. [Backend] add more content
7. [Mapbox] try out different map styles 
8. [Backend] make AWS deployable
9. [Frontend] feature: upload pictures and set markers 
10. [Frontend] feature: new marker types -> restaurant / text / etc 
