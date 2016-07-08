package org.psesd.srx.shared.core.sif

/** Enumeration of common HTTP headers.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifHttpHeader extends Enumeration {
  type SifHttpHeader = Value
  val ForwardedFor = Value("x-forwarded-for")
  val ForwardedPort = Value("x-forwarded-port")
  val ForwardedProtocol = Value("x-forwarded-proto")
  val UserAgent = Value("user-agent")
}