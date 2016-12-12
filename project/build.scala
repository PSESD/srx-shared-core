import sbt.Keys._
import sbt._

object SrxSharedCoreBuild extends Build {

  lazy val apacheHttpClientVersion = "4.5.2"
  lazy val amazonAwsVersion = "1.11.0"
  lazy val http4sVersion = "0.14.1"
  lazy val jcraftVersion = "0.1.54"
  lazy val jodaConvertVersion = "1.8.1"
  lazy val jodaTimeVersion = "2.9.4"
  lazy val json4sVersion = "3.4.0"
  lazy val scalaTestVersion = "2.2.6"
  lazy val slf4jVersion = "1.7.5"
  lazy val apacheCommonsVersion = "2.1"
  lazy val apachePoiVersion = "3.14"

  lazy val project = Project("srx-shared-core", file("."))
    .settings(
      name := "srx-shared-core",
      version := "1.0",
      scalaVersion := "2.11.8",
      libraryDependencies ++=Seq(
        "com.amazonaws" % "aws-java-sdk-s3" % amazonAwsVersion,
        "com.jcraft" % "jsch" % jcraftVersion,
        "org.apache.commons" % "commons-vfs2" % apacheCommonsVersion,
        "org.apache.httpcomponents" % "httpclient" % apacheHttpClientVersion,
        "org.apache.poi" % "poi" % apachePoiVersion,
        "org.http4s" %% "http4s-blaze-client" % http4sVersion,
        "org.http4s" %% "http4s-blaze-server" % http4sVersion,
        "org.http4s" %% "http4s-dsl" % http4sVersion,
        "org.joda" % "joda-convert" % jodaConvertVersion,
        "joda-time" % "joda-time" % jodaTimeVersion,
        "org.json4s" % "json4s-native_2.11" % json4sVersion,
        "org.json4s" % "json4s-jackson_2.11" % json4sVersion,
        "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
        "org.slf4j" % "slf4j-api" % slf4jVersion,
        "org.slf4j" % "slf4j-simple" % slf4jVersion
      )
    )

}