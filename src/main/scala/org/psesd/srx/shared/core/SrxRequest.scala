package org.psesd.srx.shared.core

import org.http4s._
import org.http4s.util.CaseInsensitiveString
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, SifRequestNotAuthorizedException}
import org.psesd.srx.shared.core.extensions.HttpTypeExtensions._
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.SifAuthenticationMethod.SifAuthenticationMethod
import org.psesd.srx.shared.core.sif.SifContentType.SifContentType
import org.psesd.srx.shared.core.sif._

import scala.xml.Node

/** Represents incoming SRX requests.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxRequest private(val sifRequest: SifRequest) {
  if (sifRequest == null) {
    throw new ArgumentNullException("sifRequest parameter")
  }

  val acceptsJson: Boolean = {
    sifRequest.accept.isDefined && sifRequest.accept.get.equals(SifContentType.Json)
  }

  val accepts: SifContentType = {
    if (acceptsJson) {
      SifContentType.Json
    } else {
      SifContentType.Xml
    }
  }

  val method: String = {
    val requestAction = sifRequest.requestAction.orNull
    if (requestAction == null) {
      ""
    } else {
      requestAction.toString
    }
  }

  val sourceIp = sifRequest.getHeader(SifHttpHeader.ForwardedFor.toString).getOrElse("")
  val userAgent = sifRequest.getHeader(SifHttpHeader.UserAgent.toString).getOrElse("")

  var errorMessage: Option[String] = None
  var errorStackTrace: Option[String] = None

  def getBodyXml: Option[Node] = {
    if (sifRequest.body.isEmpty || sifRequest.body.get.isNullOrEmpty) {
      None
    } else {
      if (sifRequest.contentType.isDefined && sifRequest.contentType.get.equals(SifContentType.Json)) {
        try {
          Some(sifRequest.body.get.toJson.toXml)
        } catch {
          case e: Exception =>
            throw new ArgumentInvalidException("request body JSON")
        }
      } else {
        try {
          Some(sifRequest.body.get.toXml)
        } catch {
          case e: Exception =>
            throw new ArgumentInvalidException("request body XML")
        }
      }
    }
  }
}

object SrxRequest {
  def apply(sifRequest: SifRequest) = new SrxRequest(sifRequest)

  def apply(provider: SifProvider, httpRequest: Request) = new SrxRequest(receiveSifRequest(provider, httpRequest))

  private def receiveSifRequest(provider: SifProvider, httpRequest: Request): SifRequest = {
    if (provider == null) {
      throw new ArgumentNullException("provider parameter")
    }
    if (httpRequest == null) {
      throw new ArgumentNullException("httpRequest parameter")
    }

    val sifUri = {
      if (httpRequest.uri.toString.startsWith("/")) {
        val hostHeader = httpRequest.headers.get(CaseInsensitiveString("host")).orNull
        val host = {
          if (hostHeader == null) {
            ""
          } else {
            hostHeader.value
          }
        }
        val isSecure = httpRequest.isSecure.getOrElse(false)
        if (isSecure) {
          new SifUri("https://" + host + httpRequest.uri.toString)
        } else {
          new SifUri("http://" + host + httpRequest.uri.toString)
        }
      } else {
        new SifUri(httpRequest.uri.toString)
      }
    }
    val resourceUri = getResourceUri(provider, sifUri)
    val zone = new SifZone(sifUri.zoneId.orNull)
    val context = new SifContext(sifUri.contextId.orNull)

    // extract authorization
    val authorizationHeader = httpRequest.headers.get(CaseInsensitiveString(SifHeader.Authorization.toString)).orNull
    if (authorizationHeader == null) {
      throw new ArgumentNullException("authorization header")
    }

    // extract timestamp
    val timestampHeader = httpRequest.headers.get(CaseInsensitiveString(SifHeader.Timestamp.toString)).orNull
    if (timestampHeader == null) {
      throw new ArgumentNullException("timestamp header")
    }
    if (!SifTimestamp.isValid(timestampHeader.value)) {
      throw new ArgumentInvalidException("timestamp header")
    }
    val timestamp = SifTimestamp(timestampHeader.value)

    // validate authorization header
    val authenticator = new SifAuthenticator(List[SifProvider](provider), List[SifAuthenticationMethod](provider.authenticationMethod))
    try {
      authenticator.validateRequestAuthorization(authorizationHeader.value, timestamp.toString)
    } catch {
      case ai: ArgumentInvalidException =>
        throw new SifRequestNotAuthorizedException(ai.getMessage.replace("parameter", "header"))

      case an: ArgumentNullOrEmptyOrWhitespaceException =>
        throw new SifRequestNotAuthorizedException(an.getMessage.replace("parameter", "header"))

      case e: Exception =>
        throw new SifRequestNotAuthorizedException(e.getMessage)
    }

    // construct SIF request
    val sifRequest = new SifRequest(provider, resourceUri, zone, context, timestamp)

    // add all received header values
    for (h <- httpRequest.headers) {
      sifRequest.addHeader(h.name.value, h.value)
    }

    // validate received headers are either empty, or contain a valid SIF value
    sifRequest.validateReceivedHeaders()

    // set SIF-specific properties
    sifRequest.accept = sifRequest.getContentType(sifRequest.getHeaderValue(SifHeader.Accept.toString))
    sifRequest.contentType = sifRequest.getContentType(sifRequest.getHeaderValue(SifHttpHeader.ContentType.toString))
    sifRequest.generatorId = sifRequest.getHeaderValueOption(SifHeader.GeneratorId.toString)
    val messageId = sifRequest.getHeaderValue(SifHeader.MessageId.toString)
    if (!messageId.isNullOrEmpty) {
      sifRequest.messageId = Option(SifMessageId(messageId))
    }
    sifRequest.messageType = SifMessageType.withNameCaseInsensitiveOption(sifRequest.getHeaderValue(SifHeader.MessageType.toString))
    sifRequest.queueId = sifRequest.getHeaderValueOption(SifHeader.QueueId.toString)
    sifRequest.requestAction = sifRequest.getRequestAction(sifRequest.getHeaderValue(SifHeader.RequestAction.toString), httpRequest.method.name)
    sifRequest.requestId = sifRequest.getHeaderValueOption(SifHeader.RequestId.toString)
    sifRequest.requestType = SifRequestType.withNameCaseInsensitiveOption(sifRequest.getHeaderValue(SifHeader.RequestType.toString))
    sifRequest.serviceType = SifServiceType.withNameCaseInsensitiveOption(sifRequest.getHeaderValue(SifHeader.ServiceType.toString))

    // set body
    sifRequest.body = Option(httpRequest.body.value)

    // ensure body is present for CREATE and UPDATE requests
    if (sifRequest.requestAction.isDefined &&
      (sifRequest.requestAction.get.equals(SifRequestAction.Create)
        || sifRequest.requestAction.get.equals(SifRequestAction.Update)) &&
      sifRequest.body.getOrElse("").isNullOrEmpty
    ) {
      throw new ArgumentInvalidException("request body")
    }

    sifRequest
  }

  private def getResourceUri(provider: SifProvider, uri: SifUri): String = {
    try {
      val minusProvider = uri.toString.replace(provider.url.toString, "").trimPrecedingSlash
      if (minusProvider.isEmpty) {
        ""
      } else {
        minusProvider.split(";").head
      }
    } catch {
      case e: Exception =>
        throw new ArgumentInvalidException("uri resource")
    }
  }

}

