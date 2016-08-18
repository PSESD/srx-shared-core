package org.psesd.srx.shared.core.exceptions

import org.psesd.srx.shared.core.sif.SifRequestAction.SifRequestAction

/** Exception for SRX resource action not allowed.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxRequestActionNotAllowedException(val requestAction: SifRequestAction, val resourceName: String) extends Exception(
  ExceptionMessage.SrxRequestActionNotAllowed.format(requestAction.toString, resourceName)
)
