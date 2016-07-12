package org.psesd.srx.shared.core.sif

/** Enumeration of SIF authentication methods.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifAuthenticationMethod extends Enumeration {
  type SifAuthenticationMethod = Value
  val Basic = Value("Basic")
  val SifHmacSha256 = Value("SIF_HMACSHA256")
}
