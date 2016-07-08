package org.psesd.srx.shared.core.config

/** Provides information about current build.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
object Build {
  val name: String = org.psesd.srx.shared.core.BuildInfo.name

  val version: String = org.psesd.srx.shared.core.BuildInfo.version

  val scalaVersion: String = org.psesd.srx.shared.core.BuildInfo.scalaVersion

  val sbtVersion: String = org.psesd.srx.shared.core.BuildInfo.sbtVersion

  val buildNumber: Int = org.psesd.srx.shared.core.BuildInfo.buildNumber

  val javaVersion: String = scala.util.Properties.javaVersion
}