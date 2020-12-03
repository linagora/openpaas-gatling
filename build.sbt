import sbt.Keys.libraryDependencies

lazy val root = (project in file("."))
  .settings(
        name := "gatling-openpaas",
        cancelable in Global := true,
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.12.8",

        libraryDependencies += "io.gatling" % "gatling-test-framework" % gatlingVersion,
        libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion
  )
  .dependsOn(jamesGatling % "compile->compile;test->test")

val gatlingVersion = "3.0.3"

enablePlugins(GatlingPlugin)

resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"
resolvers += "Fabricator" at "http://dl.bintray.com/biercoff/Fabricator"

lazy val jamesGatling = ProjectRef(uri("git://github.com/linagora/james-gatling.git"), "root")