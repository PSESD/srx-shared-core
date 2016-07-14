package org.psesd.srx.shared.core.exceptions

/** Exception for invalid Rollbar URI requests.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class RollbarNotFoundException() extends Exception(
  ExceptionMessage.RollbarNotFound
)