(defproject vocab-frontend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main vocab-frontend.core

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler
                          {:optimizations :none             ;; advanced
                           :externs ["externs.js"]
                           :output-to "resources/public/javascripts/dev.js"
                           :output-dir "resources/public/javascripts/cljs-dev/"
                           :pretty-print true
                           :source-map true}}]}

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.17"]]

  :figwheel {:css-dirs ["resources/public"]}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 [reagent "0.8.1"]
                 [hipo "0.5.2"]])



