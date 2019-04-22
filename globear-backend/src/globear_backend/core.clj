(ns globear-backend.core
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.json :refer [wrap-json-response]]
    [ring.util.response :refer [response]]))

(defn- get-marker [id]
  {:coordinates [114.162068 22.286001] :pictures ["pictures/20141229_153649.jpg"
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

(defroutes handler
           (GET "/" [] "<h1> Hello   !!!!</h1>")
           (GET "/marker" [] (response (get-marker nil))))                ;;TODO HATEOS -> should return all markers
           (route/not-found "<h1>Page not found</h1>")


(def app
  (wrap-json-response handler))


