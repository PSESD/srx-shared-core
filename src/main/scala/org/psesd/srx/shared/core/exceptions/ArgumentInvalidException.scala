package org.psesd.srx.shared.core.exceptions

/** Exception for invalid argument values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class ArgumentInvalidException(val argument: String) extends IllegalArgumentException(
  ExceptionMessage.IsInvalid.format(argument)
)
