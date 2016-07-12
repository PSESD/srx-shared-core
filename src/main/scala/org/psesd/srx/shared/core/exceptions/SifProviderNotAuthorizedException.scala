package org.psesd.srx.shared.core.exceptions

/** SIF user or session not authorized exception.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifProviderNotAuthorizedException(val sessionToken: String) extends IllegalArgumentException(
  ExceptionMessage.SifProviderNotAuthorized.format(sessionToken)
)
