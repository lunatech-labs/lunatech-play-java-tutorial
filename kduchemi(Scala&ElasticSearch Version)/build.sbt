name := """ElasticSearch Hiquea"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.194"

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-tcp" % "5.4.2"
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "5.4.2"

// testing
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-testkit" % "5.4.2" % "test"
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-embedded" % "5.4.2" % "test"

libraryDependencies += "com.nrinaudo" %% "kantan.csv" % "0.1.19"
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.1.19"

resolvers ++= Seq(
  "webjars" at "http://webjars.github.com/m2"
)

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.6.1",
  "org.webjars" % "bootstrap" % "3.3.7" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.2.1"
)
