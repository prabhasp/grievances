(defproject grievances "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3297"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [sablono "0.3.1"]
                 [onaio/milia "0.2.3-SNAPSHOT"]
                 [cljsjs/papaparse "4.1.1-1"]
                 [org.omcljs/om "0.8.8"]
                 [org.clojars.onaio/hatti "0.1.7-SNAPSHOT"]]

  :plugins [[lein-cljsbuild "1.0.5"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {:main grievances.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/grievances.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true }}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/grievances.js"
                         :main grievances.core
                         :optimizations :simple
                         :pretty-print false}}]} )
