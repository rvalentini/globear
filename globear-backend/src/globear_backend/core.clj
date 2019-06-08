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
    [globear-backend.image.thumbnail :as thumbnail]
    [globear-backend.thumbnail-service :as thumbnail-service]))


(defn- init []
  (println "Booting globear-backend")
  (println "Checking if there is a thumbnail for every image ...")
  (thumbnail-service/generate-all-thumbnails))

(def markers
  (let [raw-markers (parse-string
                      (slurp "resources/markers/markers.json") true)]
    (into {} (map
               (fn [x] {(:id x) x})
               raw-markers))))

(def not-found {:status  404
                :body    "<h1>Page not found</h1>"})


(defn- get-marker [id]
  (let [marker (get markers id)]
    (if
      (nil? marker)
      not-found
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body    marker})))

(defn- get-all-markers []
  (vals markers))


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


(defroutes handler
           (GET "/" [] "<h1> Globear-backend says hallo!</h1>")
           (GET "/markers" [] (response (get-all-markers)))
           (GET "/markers/:id" [id] (get-marker id))
           (GET "/pictures/:id" [id] (get-picture id))
           (GET "/thumbnails/:id" [id] (get-thumbnail id))
           (GET "/test" [] (thumbnail/generate-thumbnail "20141229_153649")) ;TODO remove
           (GET "/test2" [] (thumbnail-service/generate-all-thumbnails)) ;TODO remove
           (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> handler
      wrap-json-response
      (wrap-file "resources/pictures/")
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])))


(init)