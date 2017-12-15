name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.11"

libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs

libraryDependencies ++= Seq("org.webjars" % "bootstrap" % "3.3.4")
libraryDependencies ++= Seq("org.apache.commons" % "commons-lang3" % "3.5")
libraryDependencies ++= Seq("com.univocity" % "univocity-parsers" % "2.4.1")
