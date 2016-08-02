package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.{SifContext, SifMessageId, SifTimestamp, SifZone}

import scala.xml.Node

/** Represents a SRX system message.
  *
  * @version 1.0
  * @since 1.0
  * @author David S. Dennison (iTrellis, LLC)
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SrxMessage {

  def apply(service: SrxService, description: String): SrxMessage = new SrxMessage(service, SifMessageId(), SifTimestamp(), description)

  def apply(
             service: SrxService,
             resource: Option[String],
             status: Option[String],
             studentId: Option[String],
             description: String,
             srxRequest: Option[SrxRequest]
           ): SrxMessage = {
    val message = new SrxMessage(service, SifMessageId(), SifTimestamp(), description)
    message.resource = resource
    message.status = status
    message.studentId = studentId
    message.srxRequest = srxRequest
    if (srxRequest.isDefined) {
      message.generatorId = srxRequest.get.sifRequest.generatorId
      message.requestId = srxRequest.get.sifRequest.requestId
      message.method = Some(srxRequest.get.method)
      message.zone = Some(srxRequest.get.sifRequest.zone)
      message.context = Some(srxRequest.get.sifRequest.context)
    }
    message
  }

  def apply(
             service: SrxService,
             messageId: SifMessageId,
             timestamp: SifTimestamp,
             resource: Option[String],
             method: Option[String],
             status: Option[String],
             generatorId: Option[String],
             requestId: Option[String],
             zone: Option[SifZone],
             context: Option[SifContext],
             studentId: Option[String],
             description: String,
             uri: Option[String],
             userAgent: Option[String],
             sourceIp: Option[String],
             headers: Option[String],
             body: Option[String]
           ): SrxMessage = {
    val message = new SrxMessage(service, messageId, timestamp, description)
    message.resource = resource
    message.method = method
    message.status = status
    message.generatorId = generatorId
    message.requestId = requestId
    message.zone = zone
    message.context = context
    message.studentId = studentId
    message.uri = uri
    message.userAgent = userAgent
    message.sourceIp = sourceIp
    message.headers = headers
    message.body = body
    message
  }

}

class SrxMessage(val srxService: SrxService, val messageId: SifMessageId, val timestamp: SifTimestamp, val description: String) {
  if (srxService == null) {
    throw new ArgumentNullException("srxService parameter")
  }
  if (messageId == null) {
    throw new ArgumentNullException("contextId parameter")
  }
  if (timestamp == null) {
    throw new ArgumentNullException("contextId parameter")
  }
  if (description.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("description parameter")
  }

  var resource: Option[String] = None
  var method: Option[String] = None
  var status: Option[String] = None
  var generatorId: Option[String] = None
  var requestId: Option[String] = None
  var zone: Option[SifZone] = None
  var context: Option[SifContext] = None
  var studentId: Option[String] = None
  var uri: Option[String] = None
  var userAgent: Option[String] = None
  var sourceIp: Option[String] = None
  var headers: Option[String] = None
  var body: Option[String] = None
  var srxRequest: Option[SrxRequest] = None

  def toXml: Node = {
    <message>
      <messageId>{messageId.toString}</messageId>
      <timestamp>{timestamp.toString}</timestamp>
      <component>{srxService.service.name}</component>
      <componentVersion>{srxService.service.version}</componentVersion>
      <resource>{resource.getOrElse("")}</resource>
      <method>{method.getOrElse("")}</method>
      <status>{status.getOrElse("")}</status>
      <generatorId>{getGeneratorId}</generatorId>
      <requestId>{getRequestId}</requestId>
      <zoneId>{getZoneId}</zoneId>
      <contextId>{getContextId}</contextId>
      <studentId>{studentId.getOrElse("")}</studentId>
      <description>{description}</description>
      <uri>{getUri}</uri>
      <userAgent>{getUserAgent}</userAgent>
      <sourceIp>{getSourceIp}</sourceIp>
      <headers>{getHeaders}</headers>
      <body>{getBody}</body>
    </message>
  }

  def getBody: String = {
    if (body.isEmpty && srxRequest.isDefined && srxRequest.get.sifRequest.body.isDefined) {
      srxRequest.get.sifRequest.body.get
    } else {
      body.getOrElse("")
    }
  }

  def getContextId: String = {
    if (context.isDefined) {
      context.get.toString
    } else {
      ""
    }
  }

  def getGeneratorId: String = {
    if (generatorId.isDefined) {
      generatorId.get
    } else {
      ""
    }
  }

  def getHeaders: String = {
    if (headers.isEmpty && srxRequest.isDefined) {
      val sb = new StringBuilder()
      var newLine: String = ""
      for (header <- srxRequest.get.sifRequest.getHeaders) {
        sb.append("%s%s: %s".format(newLine, header._1, header._2))
        if (newLine.isEmpty) {
          newLine = "\r\n"
        }
      }
      sb.toString
    } else {
      headers.getOrElse("")
    }
  }

  def getRequestId: String = {
    if (requestId.isDefined) {
      requestId.get
    } else {
      ""
    }
  }

  def getSourceIp: String = {
    if (sourceIp.isEmpty && srxRequest.isDefined) {
      srxRequest.get.sourceIp
    } else {
      sourceIp.getOrElse("")
    }
  }

  def getUri: String = {
    if (uri.isEmpty && srxRequest.isDefined) {
      srxRequest.get.sifRequest.uri.toString
    } else {
      uri.getOrElse("")
    }
  }

  def getUserAgent: String = {
    if (userAgent.isEmpty && srxRequest.isDefined) {
      srxRequest.get.userAgent
    } else {
      userAgent.getOrElse("")
    }
  }

  def getZoneId: String = {
    if (zone.isDefined) {
      zone.get.toString
    } else {
      ""
    }
  }

}