package org.psesd.srx.shared.core

import org.http4s._
import org.http4s.util.CaseInsensitiveString
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullException, SifRequestNotAuthorizedException}
import org.psesd.srx.shared.core.extensions.HttpTypeExtensions._
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.SifAuthenticationMethod.SifAuthenticationMethod
import org.psesd.srx.shared.core.sif.SifContentType.SifContentType
import org.psesd.srx.shared.core.sif.SifRequestAction.SifRequestAction
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

  def apply(provider: SifProvider, httpRequest: Request) = new SrxRequest(getSifRequest(provider, httpRequest))

  private def getSifRequest(provider: SifProvider, httpRequest: Request): SifRequest = {
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
    val timestamp = SifTimestamp(timestampHeader.value)

    // validate authorization header
    val authenticator = new SifAuthenticator(List[SifProvider](provider), List[SifAuthenticationMethod](provider.authenticationMethod))
    try {
      authenticator.validateRequestAuthorization(authorizationHeader.value, timestamp.toString)
    } catch {
      case e: Exception =>
        throw new SifRequestNotAuthorizedException(e.getMessage)
    }

    // construct SIF request
    val sifRequest = new SifRequest(provider, resourceUri, zone, context, timestamp)

    // add all original headers
    for (h <- httpRequest.headers) {
      sifRequest.addHeader(h.name.value, h.value)
    }

    // set SIF-specific properties
    sifRequest.accept = getAccept(httpRequest)
    sifRequest.contentType = SifContentType.withNameCaseInsensitiveOption(getHeaderValue(httpRequest, SifHttpHeader.ContentType.toString))
    sifRequest.generatorId = getHeaderValueOption(httpRequest, SifHeader.GeneratorId.toString)
    val messageId = getHeaderValue(httpRequest, SifHeader.MessageId.toString)
    if (!messageId.isNullOrEmpty) {
      sifRequest.messageId = Option(SifMessageId(messageId))
    }
    sifRequest.messageType = SifMessageType.withNameCaseInsensitiveOption(getHeaderValue(httpRequest, SifHeader.MessageType.toString))
    sifRequest.queueId = getHeaderValueOption(httpRequest, SifHeader.QueueId.toString)
    sifRequest.requestAction = getRequestAction(httpRequest)
    sifRequest.requestId = getHeaderValueOption(httpRequest, SifHeader.RequestId.toString)
    sifRequest.requestType = SifRequestType.withNameCaseInsensitiveOption(getHeaderValue(httpRequest, SifHeader.RequestType.toString))
    sifRequest.serviceType = SifServiceType.withNameCaseInsensitiveOption(getHeaderValue(httpRequest, SifHeader.ServiceType.toString))

    // set body
    sifRequest.body = Option(httpRequest.body.value)

    sifRequest
  }

  def getAccept(httpRequest: Request): Option[SifContentType] = {
    val headerValue = getHeaderValue(httpRequest, SifHeader.Accept.toString)
    if (headerValue.isNullOrEmpty) {
      None
    } else {
      if (headerValue.toLowerCase.contains("json")) {
        Option(SifContentType.Json)
      } else {
        if (headerValue.toLowerCase.contains("xml")) {
          Option(SifContentType.Xml)
        } else {
          None
        }
      }
    }
  }

  def getHeaderValueOption(httpRequest: Request, name: String): Option[String] = {
    val header = httpRequest.headers.get(CaseInsensitiveString(name)).orNull
    if (header == null) {
      None
    } else {
      Option(header.value)
    }
  }

  def getRequestAction(httpRequest: Request): Option[SifRequestAction] = {
    val requestAction = SifRequestAction.withNameCaseInsensitiveOption(getHeaderValue(httpRequest, SifHeader.RequestAction.toString))
    if (requestAction.isEmpty) {
      val action = SifRequestAction.fromHttpMethod(SifHttpRequestMethod.withNameCaseInsensitive(httpRequest.method.name))
      if (action == null) {
        None
      } else {
        Option(action)
      }
    } else {
      requestAction
    }
  }

  def getHeaderValue(httpRequest: Request, name: String): String = {
    val header = httpRequest.headers.get(CaseInsensitiveString(name)).orNull
    if (header == null) {
      null
    } else {
      header.value
    }
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

