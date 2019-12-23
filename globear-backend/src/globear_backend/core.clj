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
    [globear-backend.image.image-service :as img-service]
    [globear-backend.image.thumbnail-generator :as thumbnail-generator]
    [globear-backend.image.thumbnail-service :as thumbnail-service]
    [globear-backend.marker.marker-service :as marker-service]
    [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]))


(defn- init []
  (println "Booting globear-backend")
  (println "Checking if there is a thumbnail for every image ...")
  (thumbnail-generator/generate-all-thumbnails))


(def greeting "<h1> Globear-backend says hallo!</h1>")
(def not-found {:status  404
                :body    "<h1>Page not found</h1>"})
(def internal-error {:status 500
                     :body "<h1>Internal Server Error</h1>"})


(defn- get-picture [id]
  (if (img-service/picture-exists? id)
    (let [input-stream (img-service/load-image-as-stream id)]
      (if input-stream
        {:status  200
         :headers {"Content-Type" "image/jpg"
                   "Cache-Control" "max-age=31536000, private"}
         :body    input-stream}
        internal-error))
    not-found))


(defn- get-thumbnail [id]
  (if (thumbnail-service/thumbnail-exists? id)
    (let [input-stream (thumbnail-service/load-thumbnail-as-stream id)]
      (if input-stream
        {:status  200
         :headers {"Content-Type"  "image/png"
                   "Cache-Control" "max-age=31536000, private"}
         :body input-stream}
        internal-error))
    not-found))

(defn- get-markers[]
  (let [input-stream (marker-service/get-all-markers)]
    (if input-stream
      input-stream
      internal-error)))


(defn- store-marker [body]
  (println (str "Marker received: " body))
  (let [possible-error (marker-service/save-marker body)]
    (if possible-error
      internal-error
      "OK")))


(defn- store-picture [body]
  (println "Picture received!"))


(defroutes handler
           (GET "/" [] greeting)
           (GET "/markers" [] (get-markers))
           (GET "/pictures/:id" [id] (get-picture id))
           (GET "/thumbnails/:id" [id] (get-thumbnail id))
           (PUT "/markers" {body :body} (store-marker (slurp body)))
           (PUT "/pictures" {body :body} (store-picture (slurp body)))
           (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> handler
      wrap-json-response
      (wrap-file "resources/pictures/")
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete]
                 :WWW-Authenticate "Basic realm=\"Globear\"")))

(init)