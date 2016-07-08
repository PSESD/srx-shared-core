package org.psesd.srx.shared.core.exceptions

/** Exception for null or empty argument values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class ArgumentNullOrEmptyException(val argument: String) extends NullPointerException(
  ExceptionMessage.NotNullOrEmpty.format(argument)
)