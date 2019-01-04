name := """play-java-starter-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.7"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"
libraryDependencies ++= Seq("org.apache.commons" % "commons-lang3" % "3.5")
libraryDependencies ++= Seq("com.univocity" % "univocity-parsers" % "2.4.1")
libraryDependencies ++= Seq(javaWs, jdbc)
libraryDependencies += "org.mockito" % "mockito-core" % "2.23.4" % Test
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.2"
//routesGenerator := InjectedRoutesGenerator
// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
//Ebean
lazy val myProject = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
