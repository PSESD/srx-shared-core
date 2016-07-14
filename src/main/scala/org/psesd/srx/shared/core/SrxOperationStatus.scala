package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of supported SRX operation statuses.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SrxOperationStatus extends ExtendedEnumeration {
  type SrxOperationStatus = Value
  val Completed = Value("Completed")
  val Decrypted = Value("Decrypted")
  val Decrypting = Value("Decrypting")
  val Emitted = Value("Emitted")
  val Emitting = Value("Emitting")
  val Error = Value("Error")
  val None = Value("")
  val Parsed = Value("Parsed")
  val Parsing = Value("Parsing")
  val Pending = Value("Pending")
  val Processing = Value("Processing")
  val Sending = Value("Sending")
  val Sent = Value("Sent")
  val Testing = Value("Testing")
}
