(ns globear-backend.core
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.json :refer [wrap-json-response]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.file :refer [wrap-file]]
    [ring.util.response :refer [response]]
    [clojure.java.io :as io]
    [cheshire.core :refer [parse-string]]
    [globear-backend.thumbnail-service :as thumbnail-service]
    [globear-backend.marker.marker-service :as marker-service]
    [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]))


(defn- init []
  (println "Booting globear-backend")
  (println "Checking if there is a thumbnail for every image ...")
  (thumbnail-service/generate-all-thumbnails))


(def not-found {:status  404
                :body    "<h1>Page not found</h1>"})


(defn- get-picture [id]
  (let [path (str "resources/pictures/" id ".jpg")]
    (if
      (.exists (io/file path))
      {:status  200
       :headers {"Content-Type" "image/jpg"
                 "Cache-Control" "max-age=31536000, private"}
       :body    (io/input-stream path)}
      not-found)))

(defn- get-thumbnail [id]
  (let [path (str "resources/thumbnails/" id "_thumbnail.png")]
    (if
      (.exists (io/file path))
      {:status  200
       :headers {"Content-Type" "image/png"
                 "Cache-Control" "max-age=31536000, private"}
       :body    (io/input-stream path)}
      not-found)))

(defn- store-marker [req]
  (println (str "Marker received: " req))
  (marker-service/save-marker req)
  (str "OK"))  ;;TODO implement return codes depending on succ/failure

(defroutes handler
           (GET "/" [] "<h1> Globear-backend says hallo!</h1>")
           (GET "/geojson" [] (io/input-stream "resources/markers/geojson_markers.json"))
           (GET "/pictures/:id" [id] (get-picture id))
           (GET "/thumbnails/:id" [id] (get-thumbnail id))
           (PUT "/marker" {body :body} (store-marker (slurp body)) )
           (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> handler
      wrap-json-response
      (wrap-file "resources/pictures/")
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete]
                 :WWW-Authenticate "Basic realm=\"Globear\"")
      ))


(init)