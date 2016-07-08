package org.psesd.srx.shared.core.exceptions

/** Exception for null or empty or whitespace argument values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class ArgumentNullOrEmptyOrWhitespaceException(val argument: String) extends NullPointerException(
  ExceptionMessage.NotNullOrEmptyOrWhitespace.format(argument)
)
