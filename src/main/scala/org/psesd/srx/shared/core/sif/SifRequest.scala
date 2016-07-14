package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.SifContentType.SifContentType
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
class SifRequest(provider: SifProvider,
                 resourceUri: String,
                 val zone: SifZone,
                 val context: SifContext,
                 timestamp: SifTimestamp) extends SifMessage(timestamp) {
  if (provider == null) {
    throw new ArgumentNullException("provider parameter")
  }
  if (resourceUri == null) {
    throw new ArgumentNullException("resourceUri parameter")
  }
  if (zone == null) {
    throw new ArgumentNullException("zone parameter")
  }
  if (context == null) {
    throw new ArgumentNullException("context parameter")
  }
  if (timestamp == null) {
    throw new ArgumentNullException("timestamp parameter")
  }

  def this(provider: SifProvider,
           resourceUri: String,
           zone: SifZone,
           context: SifContext) {
    this(provider, resourceUri, zone, context, SifTimestamp())
  }

  def this(provider: SifProvider,
           resourceUri: String,
           zone: SifZone) {
    this(provider, resourceUri, zone, SifContext(), SifTimestamp())
  }

  def this(provider: SifProvider,
           resourceUri: String) {
    this(provider, resourceUri, SifZone(), SifContext(), SifTimestamp())
  }

  val authorization: SifAuthorization = new SifAuthorization(provider, timestamp)

  var accept: Option[SifContentType] = Option(SifContentType.Xml)
  var generatorId: Option[String] = None
  var messageId: Option[SifMessageId] = None
  var messageType: Option[SifMessageType] = Option(SifMessageType.Request)
  var queueId: Option[String] = None
  var requestAction: Option[SifRequestAction] = None
  var requestType: Option[SifRequestType] = Option(SifRequestType.Immediate)
  var uri: SifUri = {
    SifUri(
      provider.url.toString.trimTrailingSlash
      + "/" + resourceUri.trimPrecedingSlash
      + ";zoneId=" + zone.toString
      + ";contextId=" + context.toString
    )
  }

  def getHeaders: TrieMap[String, String] = {
    addHeader(SifHeader.Accept.toString, accept.getOrElse("").toString)
    addHeader(SifHeader.Authorization.toString, authorization.toString)
    addHeader(SifHttpHeader.ContentType.toString, contentType.getOrElse("").toString)
    addHeader(SifHeader.GeneratorId.toString, generatorId.getOrElse(""))
    addHeader(SifHeader.MessageId.toString, messageId.getOrElse("").toString)
    addHeader(SifHeader.MessageType.toString, messageType.getOrElse("").toString)
    addHeader(SifHeader.QueueId.toString, queueId.getOrElse(""))
    addHeader(SifHeader.RequestAction.toString, requestAction.getOrElse("").toString)
    addHeader(SifHeader.RequestId.toString, requestId.getOrElse(""))
    addHeader(SifHeader.RequestType.toString, requestType.getOrElse("").toString)
    addHeader(SifHeader.ServiceType.toString, serviceType.getOrElse("").toString)
    addHeader(SifHeader.Timestamp.toString, timestamp.toString)
    headers
  }

}
