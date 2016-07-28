package org.psesd.srx.shared.core.exceptions

/** SIF request not authorized exception.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifRequestNotAuthorizedException(val description: String) extends SecurityException(
  description
)
