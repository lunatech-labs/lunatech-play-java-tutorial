name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.11"

javacOptions ++= Seq("-Xlint", "-Xdiags:verbose")

// to test production mode: sbt testProd
javaOptions += "-Dconfig.file=conf/prod.conf"

libraryDependencies += cache
libraryDependencies += javaWs
libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P25-B3",
  "org.webjars" % "bootstrap" % "3.3.0" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.2.1",
    // persist and query POJOs from mongo collections
   "org.mongodb.morphia" % "morphia" % "1.3.2",
   "com.github.mongobee" % "mongobee" % "0.13"
)
libraryDependencies += "com.univocity" % "univocity-parsers" % "1.5.1"

