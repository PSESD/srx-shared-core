package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.SifAccept.SifAccept
import org.psesd.srx.shared.core.sif.SifMessageType.SifMessageType
import org.psesd.srx.shared.core.sif.SifRequestAction.SifRequestAction
import org.psesd.srx.shared.core.sif.SifRequestType.SifRequestType

import scala.collection.concurrent.TrieMap

/** Represents a SIF request.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifRequest(provider: SifProvider, resourceUri: String, timestamp: SifTimestamp) extends SifMessage(timestamp) {
  if (provider == null) {
    throw new ArgumentNullException("provider parameter")
  }
  if (resourceUri == null) {
    throw new ArgumentNullException("resourceUri parameter")
  }
  if (timestamp == null) {
    throw new ArgumentNullException("timestamp parameter")
  }

  def this(provider: SifProvider, resourceUri: String) {
    this(provider, resourceUri, SifTimestamp())
  }

  val authorization: SifAuthorization = new SifAuthorization(provider, timestamp)

  var accept: Option[SifAccept] = Option(SifAccept.Xml)
  var generatorId: Option[String] = None
  var messageId: Option[SifMessageId] = None
  var messageType: Option[SifMessageType] = Option(SifMessageType.Request)
  var requestAction: Option[SifRequestAction] = None
  var requestType: Option[SifRequestType] = Option(SifRequestType.Immediate)
  var uri: SifUri = {
    SifUri(provider.baseUri.toString.trimTrailingSlash + "/" + resourceUri.trimPrecedingSlash)
  }

  def getHeaders: TrieMap[String, String] = {
    addHeader(SifHeader.Accept.toString, accept.getOrElse("").toString)
    addHeader(SifHeader.Authorization.toString, authorization.toString)
    addHeader(SifHeader.GeneratorId.toString, generatorId.getOrElse(""))
    addHeader(SifHeader.MessageId.toString, messageId.getOrElse("").toString)
    addHeader(SifHeader.MessageType.toString, messageType.getOrElse("").toString)
    addHeader(SifHeader.RequestAction.toString, requestAction.getOrElse("").toString)
    addHeader(SifHeader.RequestId.toString, requestId.getOrElse(""))
    addHeader(SifHeader.RequestType.toString, requestType.getOrElse("").toString)
    addHeader(SifHeader.ServiceType.toString, serviceType.getOrElse("").toString)
    addHeader(SifHeader.Timestamp.toString, timestamp.toString)
    headers
  }

}
