package org.psesd.srx.shared.core.logging

/** Enumeration of logging levels.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object LogLevel extends Enumeration {
  type LogLevel = Value
  val Debug = Value("debug")
  val Error = Value("error")
  val Info = Value("info")
  val Warning = Value("warning")
}
