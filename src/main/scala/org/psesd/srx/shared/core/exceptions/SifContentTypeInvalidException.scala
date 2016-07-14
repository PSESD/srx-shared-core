package org.psesd.srx.shared.core.exceptions

/** SIF request or response body does not match specified content type.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifContentTypeInvalidException(val description: String) extends IllegalArgumentException(
  description
)
