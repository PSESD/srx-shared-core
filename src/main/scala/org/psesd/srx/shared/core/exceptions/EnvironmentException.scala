package org.psesd.srx.shared.core.exceptions

/** Environment configuration exception.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class EnvironmentException(val description: String) extends IllegalArgumentException(
  description
)
