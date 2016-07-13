package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif.SifServiceType.SifServiceType

import scala.collection.concurrent.TrieMap

/** Represents a SIF message.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifMessage(val timestamp: SifTimestamp) {
  if (timestamp == null) {
    throw new ArgumentNullException("timestamp parameter")
  }
  val headers = new TrieMap[String, String]
  var body: Option[String] = None
  var requestId: Option[String] = None
  var serviceType: Option[SifServiceType] = Option(SifServiceType.Object)
}
