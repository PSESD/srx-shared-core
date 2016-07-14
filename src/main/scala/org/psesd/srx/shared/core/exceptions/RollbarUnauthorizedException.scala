package org.psesd.srx.shared.core.exceptions

/** Exception for unauthorized Rollbar requests.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class RollbarUnauthorizedException() extends SecurityException(
  ExceptionMessage.RollbarUnauthorized
)
