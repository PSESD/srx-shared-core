package org.psesd.srx.shared.core.exceptions

/** Exception for requested SRX resource not found.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxResourceNotFoundException(val resourceName: String) extends Exception(
  ExceptionMessage.SrxResourceNotFound.format(resourceName)
)
