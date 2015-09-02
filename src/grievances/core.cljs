(ns ^:figwheel-always grievances.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [cljs.core.async :refer [<! put!]]
            [milia.api.http :refer [parse-http]]
            [hatti.ona.csv-reader :refer [progressively-read-csv!]]
            [hatti.ona.forms :refer [flatten-form]]
            [hatti.ona.post-process :refer [integrate-attachments!]]
            [hatti.routing :as routing]
            [hatti.shared :as shared]
            [hatti.utils :refer [json->cljs]]
            [hatti.views :as views]
            [hatti.views.dataview]))

(enable-console-print!)

(def mapbox-tiles
  [{:url "http://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png"
    :name "Humanitarian OpenStreetMap Team"
    :attribution "&copy;  <a href=\"http://osm.org/copyright\">
                  OpenStreetMap Contributors.</a>
                  Tiles courtesy of
                  <a href=\"http://hot.openstreetmap.org/\">
    Humanitarian OpenStreetMap Team</a>."}])
(defn data [f] (str "resources/public/data/" f))

(go
 (let [data-chan (parse-http :get (data "citizen_grievances.csv"))
       form-chan (parse-http :get (data "form.json"))
       info-chan (parse-http :get (data "info.json"))
       form (-> (<! form-chan) :body flatten-form)
       info (-> (<! info-chan) :body)
       reader (fn [data completed?]
                (shared/add-to-app-data! shared/app-state data :completed? completed?))]
   (shared/transact-app-state! shared/app-state [:views :all] (fn [_] [:map]))
   (shared/transact-app-state! shared/app-state [:dataset-info] (fn [_] info))
   (integrate-attachments! shared/app-state form)
   (om/root views/tabbed-dataview
            shared/app-state
            {:target (. js/document (getElementById "app"))
             :shared {:flat-form form
                      :map-config {:mapbox-tiles mapbox-tiles}}
             :opts {:chart-get identity}})
   (progressively-read-csv! (:body (<! data-chan)) reader)))
(routing/enable-dataview-routing!)
