package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif._

/** Provides SRX system message service methods.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object SrxMessageService {

  def createMessage(generatorId: String, srxMessage: SrxMessage): SifResponse = {
    val sifRequest = new SifRequest(Environment.srxProvider, SrxResourceType.SrxMessages.toString)
    sifRequest.requestId = Some(SifMessageId().toString)
    sifRequest.generatorId = Some(generatorId)
    sifRequest.contentType = Some(SifContentType.Json)
    sifRequest.accept = Some(SifContentType.Json)
    sifRequest.serviceType = Some(SifServiceType.Object)
    sifRequest.messageType = Some(SifMessageType.Request)
    sifRequest.requestType = Some(SifRequestType.Immediate)
    sifRequest.body = Some(srxMessage.toXml.toJsonString)

    new SifConsumer().create(sifRequest)
  }

  def createRequestMessage(method: String,
                           zoneId: String,
                           studentId: String,
                           parameters: SifRequestParameterCollection,
                           service: SrxService,
                           resource: Option[String],
                           requestBody: Option[String]
                          ):SifResponse = {

    val message = SrxMessage(
      service,
      SifMessageId(),
      SifTimestamp(),
      resource,
      Some(method),
      Some(SrxMessageStatus.Success.toString),
      parameters("generatorId"),
      parameters("requestId"),
      Some(SifZone(zoneId)), {
        if (parameters("contextId").isDefined) Some(SifContext(parameters("contextId").get)) else None
      },
      Some(studentId),
      "%s successful for student '%s' in zone '%s'.".format(method, studentId, zoneId),
      parameters("uri"),
      parameters("userAgent"),
      parameters("sourceIp"),
      Some(parameters.getHeaders()),
      requestBody
    )

    createMessage(parameters("generatorId").get, message)
  }

  def queryMessage(resource: String, zone: SifZone, context: SifContext): SifResponse = {
    val sifRequest = new SifRequest(Environment.srxProvider, resource, zone, context)
    sifRequest.accept = Some(SifContentType.Xml)

    new SifConsumer().query(sifRequest)
  }
}
