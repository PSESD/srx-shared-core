package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of supported SRX message statuses.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SrxMessageStatus extends ExtendedEnumeration {
  type SrxMessageStatus = Value
  val BadRequest = Value("bad request")
  val Error = Value("error")
  val NotFound = Value("not found")
  val Success = Value("success")
}
