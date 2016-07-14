package org.psesd.srx.shared.core.exceptions

/** SIF authentication method not valid exception.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifAuthenticationMethodInvalidException(val method: String) extends SecurityException(
  ExceptionMessage.SifAuthenticationMethodInvalid.format(method)
)
