package org.psesd.srx.shared.core.exceptions

/** Exception for null argument values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class ArgumentNullException(val argument: String) extends NullPointerException(
  ExceptionMessage.NotNull.format(argument)
)
