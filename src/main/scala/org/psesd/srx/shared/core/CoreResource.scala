package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of supported SRX operations.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object CoreResource extends ExtendedEnumeration {
  type CoreResource = Value
  val Info = Value("info")
  val Ping = Value("ping")
  val SrxMessages = Value("srxMessages")
}