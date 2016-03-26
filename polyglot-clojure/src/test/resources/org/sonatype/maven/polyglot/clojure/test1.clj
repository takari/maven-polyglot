;;
;; Copyright (c) 2015 to original author or authors
;; All rights reserved. This program and the accompanying materials
;; are made available under the terms of the Eclipse Public License v1.0
;; which accompanies this distribution, and is available at
;; http://www.eclipse.org/legal/epl-v10.html
;;

(defmaven 'a/b "c"

 :name "Polyglot Test"
 :description "Maven 3 Clojure based Polyglot test"
 :properties {:test "test"}

 :dependency-management [:dependencies [['org.apache.maven/apache-maven "${mavenVersion}" :classifier "bin" :type "zip"]]]

 :dependencies [['org.clojure/clojure "1.1.0-alpha-SNAPSHOT"]
                'org.clojure/clojure-contrib]
 :test-dependencies [['org.junit/junit "4.0"]]

 :build [:plugins [['org.apache.maven.plugins/maven-compiler-plugin :source "1.5" :target "1.5"]

                   ['com.theoryinpractise/clojure-maven-plugin "1.2-SNAPSHOT" :testScript "src/test/clojure/test.clj"
                    :executions [[:id "compile" :phase "compile" :goals ["compile"]]
                                 [:id "test" :phase "test" :goals ["test"]]]]
                   ]])
