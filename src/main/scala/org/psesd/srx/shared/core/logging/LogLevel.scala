package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of logging levels.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object LogLevel extends ExtendedEnumeration {
  type LogLevel = Value
  val Critical = Value("critical")
  val Debug = Value("debug")
  val Error = Value("error")
  val Info = Value("info")
  val Local = Value("local")
  val Warning = Value("warning")
}
