name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.11"

javacOptions ++= Seq("-Xlint", "-Xdiags:verbose")


libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs

libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P25-B3",
  "org.webjars" % "bootstrap" % "3.3.0" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.2.1"
)

//libraryDependencies += "com.h2database" % "h2" % "1.3.148" % "test"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"

libraryDependencies += "com.univocity" % "univocity-parsers" % "1.5.1"
//playEbeanDebugLevel := 4
