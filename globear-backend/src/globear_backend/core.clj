(ns globear-backend.core
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.json :refer [wrap-json-response]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.file :refer [wrap-file]]
    [ring.util.response :refer [response]]
    [clojure.java.io :as io]))

(defn- get-marker [id]
  {:coordinates [103.865158 1.354875] :pictures ["pictures/20141229_153649.jpg"
                                                 "pictures/20141229_193757.jpg"
                                                 "pictures/20141229_205946.jpg"
                                                 "pictures/20141229_214911.jpg"
                                                 "pictures/20141230_115855.jpg"
                                                 "pictures/20141230_121228.jpg"
                                                 "pictures/20141230_123537.jpg"
                                                 "pictures/20141230_134534.jpg"
                                                 "pictures/20141230_135253.jpg"
                                                 "pictures/20141230_205948.jpg"
                                                 "pictures/20141230_230607.jpg"
                                                 "pictures/20141231_131014.jpg"]})



(defn- get-picture [id]
  (let [path (str "resources/pictures/" id ".jpg")]
    (if
      (.exists (io/file path))
      {:status  200
       :headers {"Content-Type" "image/jpg"}
       :body    (io/input-stream path)}
      {:status  404
       :body    "File not found!"})))


(defroutes handler
           (GET "/" [] "<h1> Hello!!!!</h1>")
           (GET "/marker" [] (response (get-marker nil)))   ;;TODO HATEOS -> should return all markers
           (GET "/picture/:id" [id] (get-picture id))
           (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> handler
      wrap-json-response
      (wrap-file "resources/pictures/")
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])))


