(defproject cljs_24_hour_clock "0.1.0-SNAPSHOT"
  :description "A 24-hour clock-face"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"],
                 [org.clojure/clojurescript "0.0-2227"]]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :source-paths ["src/cljs" "src/clj"]
  :cljsbuild {
    :builds [{
        :source-paths ["src/cljs"]
        :compiler {
          :output-to "public/js/clockface.js"
          :optimizations :whitespace
          :pretty-print true}}]})
