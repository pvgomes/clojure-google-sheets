(defproject clojure-google-sheets "0.1.0-SNAPSHOT"
  :description "clojure google sheets api integration"
  :url "https://github.com/pvgomes/clojure-google-sheets"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-time "0.12.2"]
                 [com.google.api-client/google-api-client "1.28.0"]
                 [com.google.oauth-client/google-oauth-client-jetty "1.28.0"]
                 [com.google.apis/google-api-services-sheets "v4-rev566-1.25.0"]]
  :main ^:skip-aot clojure-google-sheets.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})