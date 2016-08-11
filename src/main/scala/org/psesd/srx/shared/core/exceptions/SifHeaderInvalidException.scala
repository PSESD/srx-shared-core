package org.psesd.srx.shared.core.exceptions

/** SIF header contains invalid value.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifHeaderInvalidException(val headerName: String, val headerValue: String) extends IllegalArgumentException(
  ExceptionMessage.SifHeaderInvalid.format(headerName, headerValue)
)
