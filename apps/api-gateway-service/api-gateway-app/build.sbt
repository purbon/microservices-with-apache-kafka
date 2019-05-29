name := """api-gateway-app"""
organization := "com.purbon"
maintainer := "pere.urbon@acm.org"

version := "1.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

val kafkaVersion = "2.2.0"
val jacksonVersion = "2.9.9"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += ehcache


libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test
libraryDependencies += "org.apache.kafka" % "kafka-clients" % kafkaVersion
libraryDependencies += "org.apache.kafka" % "kafka-streams" % kafkaVersion

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.purbon.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.purbon.binders._"
