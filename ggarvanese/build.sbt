name := """play-java-starter-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

libraryDependencies ++= Seq("org.apache.commons" % "commons-lang3" % "3.5")
libraryDependencies ++= Seq("com.univocity" % "univocity-parsers" % "2.4.1")
libraryDependencies ++= Seq(javaWs)
libraryDependencies ++= Seq("org.mockito" % "mockito-core" % "2.8.47")
libraryDependencies ++= Seq("com.fasterxml.jackson.core" % "jackson-core" % "2.9.8")
libraryDependencies ++= Seq("commons-io" % "commons-io" % "2.6")
libraryDependencies ++= Seq("com.google.guava" % "guava" % "19.0")


// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))