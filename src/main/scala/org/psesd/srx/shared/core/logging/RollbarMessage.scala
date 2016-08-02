package org.psesd.srx.shared.core.logging

import java.util.UUID

import org.joda.time._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import org.psesd.srx.shared.core.SrxMessage
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.logging.LogLevel.LogLevel
import org.psesd.srx.shared.core.sif.SifHeader

/** Represents a Rollbar item message.
  *
  * @version 1.0
  * @since 1.0
  * @author David S. Dennison (iTrellis, LLC)
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class RollbarMessage(srxMessage: SrxMessage, logLevel: LogLevel) {

  if (srxMessage == null) {
    throw new ArgumentNullException("srxMessage parameter")
  }

  if (logLevel == null) {
    throw new ArgumentNullException("logLevel parameter")
  }

  private final val BodyAttributesToken = "BODY_ATTRIBUTES_7b58b4a1-b95c-43f7-9f29-dfac8ee49784_TOKEN"
  private final val HeadersToken = "HEADERS_6b129e50-5f84-460d-b548-9d8464ab98c6_TOKEN"
  private final val RollbarAccessTokenKey = "ROLLBAR_ACCESS_TOKEN"
  private final val ServerNameKey = "SERVER_NAME"

  private implicit val formats = Serialization.formats(NoTypeHints)

  private val rollbarAccessToken = Environment.getProperty(RollbarAccessTokenKey)
  private val serverName = Environment.getProperty(ServerNameKey)

  def getJsonString(): String = {
    val version = srxMessage.srxService.service.version
    val itemId = UUID.randomUUID.toString
    val timestamp = Instant.now.getMillis
    val title = srxMessage.description
    val messageBody = srxMessage.body.getOrElse("")
    val lowerCaseLevel = logLevel.toString.toLowerCase

    val itemMessage = new message(messageBody, BodyAttributesToken)
    val itemBody = new body(itemMessage)
    val itemServer = new server(serverName)
    val itemRequest = getRequest(srxMessage)
    val itemData = new data(itemBody, timestamp, itemId, "scala", itemRequest, Environment.name, title, lowerCaseLevel, version, itemServer)
    val item = new json(rollbarAccessToken, itemData)

    val itemJson = write(item)

    getItemWithCustomAttributes(srxMessage, itemJson)
  }

  private def getRequest(message: SrxMessage): request = {
    var url = ""
    var method = ""
    var body = ""
    var userIp = ""
    if (message.srxRequest.isDefined) {
      url = message.srxRequest.get.sifRequest.uri.toString
      method = message.srxRequest.get.method
      body = message.srxRequest.get.sifRequest.body.getOrElse("")
      userIp = message.srxRequest.get.sourceIp
    }
    new request(url, method, HeadersToken, body, userIp)
  }

  private def getItemWithCustomAttributes(message: SrxMessage, itemJson: String): String = {
    // inject custom message keys
    val attributes = new StringBuilder("")
    attributes.append(",\"message_id\":%s".format(write(message.messageId.toString)))
    attributes.append(",\"timestamp\":%s".format(write(message.timestamp.toString)))
    attributes.append(",\"component\":%s".format(write(message.srxService.service.name)))
    attributes.append(",\"component_version\":%s".format(write(message.srxService.service.version)))
    attributes.append(",\"resource\":%s".format(write(message.resource.getOrElse("").toString)))
    attributes.append(",\"method\":%s".format(write(message.method.getOrElse("").toString)))
    attributes.append(",\"status\":%s".format(write(message.status.getOrElse("").toString)))
    attributes.append(",\"generator_id\":%s".format(write(message.generatorId.getOrElse(""))))
    attributes.append(",\"request_id\":%s".format(write(message.requestId.getOrElse(""))))
    attributes.append(",\"zone_id\":%s".format(write(message.getZoneId)))
    attributes.append(",\"context_id\":%s".format(write(message.getContextId)))
    attributes.append(",\"student_id\":%s".format(write(message.studentId.getOrElse(""))))
    attributes.append(",\"user_agent\":%s".format(write(message.userAgent.getOrElse(""))))
    if(message.srxRequest.isEmpty) {
      attributes.append(",\"message_body\":%s".format(write(message.body.getOrElse(""))))
    }

    if (message.srxRequest.isDefined) {
      if (message.srxRequest.get.errorMessage.isDefined) {
        attributes.append(",\"error_message\":%s".format(write(message.srxRequest.get.errorMessage.get)))
      }
      if (message.srxRequest.get.errorStackTrace.isDefined) {
        attributes.append(",\"error_stack_trace\":%s".format(write(message.srxRequest.get.errorStackTrace.get)))
      }
    }

    // inject originating request headers
    val headers = new StringBuilder("{")
    if (message.srxRequest.isDefined) {
      var sep = ""
      for ((key, value) <- message.srxRequest.get.sifRequest.getHeaders) {
        // do not write restricted headers to Rollbar
        if(!SifHeader.isRestricted(key)) {
          headers.append("%s%s:%s".format(sep, write(key), write(value)))
          sep = ","
        }
      }
    }
    headers.append("}")

    itemJson.replace(",\"attributes\":\"%s\"".format(BodyAttributesToken), attributes.toString).replace("\"%s\"".format(HeadersToken), headers.toString)
  }

  /** Body portion of a Rollbar JSON payload.
    *
    * @constructor creates a new `body` instance.
    * @param message the `message` instance that contains the Rollbar JSON payload message body content.
    * @return a new `body` instance.
    * */
  private case class body(message: message)

  /** Data portion of a Rollbar JSON payload.
    *
    * @constructor creates a new `data` instance.
    * @param body         the `body` portion of a Rollbar JSON payload.
    * @param timestamp    the millisecond representation of data timestamp.
    * @param uuid         the unique data message ID.
    * @param framework    description of the application stack (i.e. `scala/akka/spray`).
    * @param request      the `request` portion of a Rollbar JSON payload.
    * @param environment  the current environment name.
    * @param title        the data message title.
    * @param level        the associated log level.
    * @param code_version the current version of the codebase.
    * @param server       the `server` portion of a Rollbar JSON payload.
    * @return a new `data` instance.
    * */
  private case class data(body: body, timestamp: Long, uuid: String, framework: String, request: request, environment: String, title: String, level: String, code_version: String, server: server)

  /** Top-level portion of a Rollbar JSON payload.
    *
    * @constructor creates a new `json` instance.
    * @param access_token the Rollbar API access token.
    * @param data         the `data` portion of a Rollbar JSON payload.
    * @return a new `json` instance.
    * */
  private case class json(access_token: String, data: data)

  /** Message body portion of a Rollbar JSON payload.
    *
    * @constructor creates a new `message` instance.
    * @param body       the JSON payload message body content.
    * @param attributes custom attributes specific to the adapter.
    * @return a new `message` instance.
    * */
  private case class message(body: String, attributes: String)

  /** Request portion of a Rollbar JSON payload.
    *
    * @constructor creates a new `request` instance.
    * @param url     the originating request url.
    * @param method  the originating request method (GET/POST).
    * @param headers the originating request headers and values.
    * @param body    the originating request body.
    * @param user_ip the originating request ip address.
    * @return a new `request` instance.
    * */
  private case class request(url: String, method: String, headers: String, body: String, user_ip: String)

  /** Server portion of a Rollbar JSON payload.
    *
    * @constructor creates a new `server` instance.
    * @param host the server host name.
    * @return a new `server` instance.
    * */
  private case class server(host: String)

}