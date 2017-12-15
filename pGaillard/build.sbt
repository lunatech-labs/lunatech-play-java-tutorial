name := """ElasticWithPlay"""
organization := "Lunatech"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Confluent" at "http://packages.confluent.io/maven/"
)

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http" % "5.4.2"

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "5.4.2"

// testing
libraryDependencies +="com.sksamuel.elastic4s" %% "elastic4s-testkit" % "5.4.2" % "test"
libraryDependencies +="com.sksamuel.elastic4s" %% "elastic4s-embedded" % "5.4.2" % "test"
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "Lunatech.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "Lunatech.binders._"

libraryDependencies +="com.nrinaudo" %% "kantan.csv" % "0.1.19"
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.1.19"

libraryDependencies ++= Seq("org.apache.commons" % "commons-lang3" % "3.5")

libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"