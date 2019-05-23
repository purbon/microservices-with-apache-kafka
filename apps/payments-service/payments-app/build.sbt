name := """payments-app"""
organization := "com.purbon"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.2.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.purbon.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.purbon.binders._"