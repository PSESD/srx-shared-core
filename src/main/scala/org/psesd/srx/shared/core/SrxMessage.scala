package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.SrxOperation.SrxOperation
import org.psesd.srx.shared.core.SrxOperationStatus.SrxOperationStatus
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.sif.{SifMessageId, SifTimestamp}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.xml.Elem

/** Represents a SRX system message.
  *
  * @version 1.0
  * @since 1.0
  * @author David S. Dennison (iTrellis, LLC)
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object SrxMessage {

  private val messageFields = List(
    "ServiceName",
    "ServiceBuild",
    "MessageId",
    "Timestamp",
    "Operation",
    "Status",
    "Source",
    "Destination",
    "Description",
    "Body",
    "SourceIP",
    "UserAgent"
  )

  /** Returns an empty `Message` instance */
  def getEmpty(service: SrxService): SrxMessage = new SrxMessage(None, SifTimestamp(), service, None, None, None, None, None, None, None, None, None)

  def fromString(s: String, srxService: SrxService): SrxMessage = {
    if (s.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("message value")
    }

    if (!isMessage(s)) {
      throw new ArgumentInvalidException("message value")
    }

    val mapping: Map[String, String] = mapMessage(s)

    new SrxMessage(
      Option(SifMessageId(mapping("messageid"))),
      SifTimestamp(mapping("timestamp")),
      srxService,
      Option(SrxOperation.withNameCaseInsensitive(mapping("operation"))),
      Option(SrxOperationStatus.withNameCaseInsensitive(mapping("status"))),
      Option(mapping("source")),
      Option(mapping("destination")),
      Option(mapping("description")),
      Option(mapping("body")),
      Option(mapping("sourceip")),
      Option(mapping("useragent")),
      None)
  }

  def isMessage(s: String): Boolean = {

    if (s.isNullOrEmpty) {
      false
    } else {
      val mapping: Map[String, String] = mapMessage(s)

      if (mapping.isEmpty) {
        false
      } else {
        val hasRequiredFields = messageFields.forall { key =>
          mapping.contains(key.toLowerCase) && !mapping(key.toLowerCase).isNullOrEmpty
        }

        if (hasRequiredFields) {
          val hasValidMessageId = SifMessageId.isValid(mapping("messageid"))
          val hasValidTimestamp = SifTimestamp.isValid(mapping("timestamp"))
          val hasServiceName = SifTimestamp.isValid(mapping("serviceName"))
          val hasServiceBuild = SifTimestamp.isValid(mapping("serviceBuild"))

          hasRequiredFields && hasValidMessageId && hasValidTimestamp && hasServiceName && hasServiceBuild
        } else {
          false
        }
      }
    }
  }

  def mapMessage(s: String): Map[String, String] = {

    if (s.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("value to map")
    }

    if (!s.contains(',') || !s.contains(':') || s.split(',').length != 10) {
      Map[String, String]()
    } else {
      s.split(',').foldLeft(Map[String, String]()) {
        (map, value) =>

          val pair: Array[String] = if (value.trim.toLowerCase.startsWith("timestamp: ")) {
            value.split(": ")
          } else {
            value.split(':')
          }

          pair.length match {
            case 1 => map + (pair.head.trim.toLowerCase -> "")
            case 2 => map + (pair.head.trim.toLowerCase -> pair.tail.head.trim)
            case _ => map
          }
      }
    }
  }
}

case class SrxMessage(messageId: Option[SifMessageId],
                      timestamp: SifTimestamp,
                      srxService: SrxService,
                      operation: Option[SrxOperation],
                      status: Option[SrxOperationStatus],
                      source: Option[String],
                      destination: Option[String],
                      description: Option[String],
                      body: Option[String],
                      sourceIp: Option[String],
                      userAgent: Option[String],
                      srxRequest: Option[SrxRequest]) {

  /** @return `true` if the current instance is empty; otherwise `false`.
    * @note 'Empty' means:
    *       Either MessageId or Timestamp are empty.
    *       - or -
    *       The message contains a valid MessageId and Timestamp, but all of its fields are empty.
    * */
  def isEmpty: Boolean = {
    val timestampOp: Option[SifTimestamp] = Option(timestamp).flatMap(Option(_))
    if (messageId.isEmpty || timestampOp.isEmpty) {
      true
    }
    else {
      operation.isEmpty &&
        status.isEmpty &&
        source.isEmpty &&
        destination.isEmpty &&
        description.isEmpty &&
        body.isEmpty &&
        sourceIp.isEmpty &&
        userAgent.isEmpty &&
        srxRequest.isEmpty
    }
  }

  def toXml: Elem = {
    <message>
      <messageId>{messageId.getOrElse(SifMessageId().toString)}</messageId>
      <timestamp>{timestamp.toString}</timestamp>
      <serviceName>{srxService.service.name}</serviceName>
      <serviceBuild>{srxService.service.version}</serviceBuild>
      <messageId>{messageId.getOrElse(SifMessageId().toString)}</messageId>
      <operation>{operation.getOrElse("None")}</operation>
      <status>{status.getOrElse("None")}</status>
      <source>{source.getOrElse("None")}</source>
      <destination>{destination.getOrElse("None")}</destination>
      <description>{description.getOrElse("None")}</description>
      <body>{body.getOrElse("")}</body>
      <sourceIp>{sourceIp.getOrElse("None")}</sourceIp>
      <userAgent>{userAgent.getOrElse("None")}</userAgent>
    </message>
  }

  override def toString: String = {
      "MessageId: " + messageId.getOrElse(SifMessageId().toString) +
      ", Timestamp: " + timestamp.toString +
      ", ServiceName: " + srxService.service.name +
      ", ServiceBuild: " + srxService.service.version +
      ", Operation: " + operation.getOrElse(SrxOperation.None) +
      ", Status: " + status.getOrElse(SrxOperationStatus.None) +
      ", Source: " + source.getOrElse("None") +
      ", Destination: " + destination.getOrElse("None") +
      ", Description: " + description.getOrElse("None") +
      ", Body: " + body.getOrElse("") +
      ", SourceIP: " + sourceIp.getOrElse("None") +
      ", UserAgent: " + userAgent.getOrElse("None")
  }
}