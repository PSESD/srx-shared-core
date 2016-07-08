package org.psesd.srx.shared.core.exceptions

/** Exception for unhandled Rollbar request failures.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class RollbarUnhandledException(statusCode: Int) extends IllegalArgumentException(
  ExceptionMessage.RollbarUnhandled.format(statusCode.toString)
)
