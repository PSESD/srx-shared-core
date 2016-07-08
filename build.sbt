name := "srx-shared-core"

version := "1.0"

scalaVersion := "2.11.8"

lazy val apacheHttpClientVersion = "4.5.2"
lazy val http4sVersion = "0.14.1"
lazy val jodaConvertVersion = "1.8.1"
lazy val jodaTimeVersion = "2.9.4"
lazy val json4sVersion = "3.4.0"
lazy val scalaTestVersion = "2.2.6"
lazy val slf4jVersion = "1.7.5"

// Date/time
libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % jodaTimeVersion,
  "org.joda" % "joda-convert" % jodaConvertVersion
)

// Logging
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "org.slf4j" % "slf4j-simple" % slf4jVersion
)

// Test
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
)

// JSON
libraryDependencies ++= Seq(
  "org.json4s" % "json4s-native_2.11" % json4sVersion
)

// HTTP Client
libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % apacheHttpClientVersion
)

// HTTP Server
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

// Build info
lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, BuildInfoKey.map(buildInfoBuildNumber) { case (k, v) =>
      "buildNumber" -> v
    }),
    buildInfoPackage := "org.psesd.srx.shared.core"
  )

