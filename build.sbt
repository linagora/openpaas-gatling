name := "gatling-openpaas"
cancelable in Global := true

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.8"

enablePlugins(GatlingPlugin)

libraryDependencies += "io.gatling" % "gatling-test-framework" % "3.0.3"
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.3"

resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"
resolvers += "Fabricator" at "http://dl.bintray.com/biercoff/Fabricator"