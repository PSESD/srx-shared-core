package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of SIF authentication methods.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifAuthenticationMethod extends ExtendedEnumeration {
  type SifAuthenticationMethod = Value
  val Basic = Value("Basic")
  val SifHmacSha256 = Value("SIF_HMACSHA256")
}
