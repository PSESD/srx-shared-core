package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif.SifMessageType.SifMessageType

import scala.collection.concurrent.TrieMap

/** Represents a SIF response.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifResponse(timestamp: SifTimestamp,
                  val messageId: SifMessageId,
                  val messageType: SifMessageType,
                  val sifRequest: SifRequest) extends SifMessage(timestamp) {
  if (timestamp == null) {
    throw new ArgumentNullException("timestamp parameter")
  }
  if (messageId == null) {
    throw new ArgumentNullException("messageId parameter")
  }
  if (messageType == null) {
    throw new ArgumentNullException("messageType parameter")
  }
  if (sifRequest == null) {
    throw new ArgumentNullException("sifRequest parameter")
  }

  val responseAction = sifRequest.requestAction.orElse(None)
  var statusCode: Int = 0

  def getHeaders: TrieMap[String, String] = {
    addHeader(SifHttpHeader.ContentType.toString, contentType.getOrElse("").toString)
    addHeader(SifHeader.MessageId.toString, messageId.toString)
    addHeader(SifHeader.MessageType.toString, messageType.toString)
    addHeader(SifHeader.ResponseAction.toString, responseAction.getOrElse("").toString)
    addHeader(SifHeader.RequestId.toString, requestId.getOrElse(""))
    addHeader(SifHeader.ServiceType.toString, serviceType.getOrElse("").toString)
    addHeader(SifHeader.Timestamp.toString, timestamp.toString)
    headers
  }
}
