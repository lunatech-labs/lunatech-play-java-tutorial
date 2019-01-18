import com.typesafe.sbt.web.SbtWeb.autoImport._

name := """play-java-starter-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Add Less
includeFilter in (Assets, LessKeys.less) := "*.less"

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// This is for CSV parsing
libraryDependencies ++= Seq("com.univocity" % "univocity-parsers" % "2.4.1")

// For Unit Testing with Mockito
libraryDependencies ++= Seq("org.mockito" % "mockito-core" % "2.8.47")
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.6" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Add Evolutions for Database
libraryDependencies ++= Seq(evolutions, jdbc, javaWs)
