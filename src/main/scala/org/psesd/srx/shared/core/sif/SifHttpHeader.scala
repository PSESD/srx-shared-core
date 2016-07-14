package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of common HTTP headers.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifHttpHeader extends ExtendedEnumeration {
  type SifHttpHeader = Value
  val ContentType = Value("Content-Type")
  val ForwardedFor = Value("x-forwarded-for")
  val ForwardedPort = Value("x-forwarded-port")
  val ForwardedProtocol = Value("x-forwarded-proto")
  val UserAgent = Value("user-agent")
}