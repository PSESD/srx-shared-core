package org.psesd.srx.shared.core

import org.http4s.Header.Raw
import org.http4s._
import org.http4s.headers.`Content-Type`
import org.http4s.util.CaseInsensitiveString
import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.extensions.HttpTypeExtensions._
import org.psesd.srx.shared.core.sif._

import scalaz.concurrent.Task

/** Represents outgoing SRX response.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxResponse(val srxRequest: SrxRequest) {
  if (srxRequest == null) {
    throw new ArgumentNullException("srxRequest parameter")
  }

  val sifResponse = new SifResponse(SifTimestamp(), SifMessageId(), SifMessageType.Response, srxRequest.sifRequest)

  def setError(sifError: SifError): Unit = {
    sifResponse.statusCode = sifError.code
    sifResponse.error = Option(sifError)
  }

  def toHttpResponse: Task[Response] = {
    val httpStatus = Status.fromInt(sifResponse.statusCode).valueOr(null)
    var httpHeaders = Headers(
      Raw(CaseInsensitiveString("Access-Control-Allow-Origin"), "*"),
      getHttpContentType,
      Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), sifResponse.timestamp.toString),
      Raw(CaseInsensitiveString(SifHeader.MessageId.toString), sifResponse.messageId.toString)
    )
    if (hasError) {
      httpHeaders = httpHeaders.put(Raw(CaseInsensitiveString(SifHeader.MessageType.toString), SifMessageType.Error.toString))
    } else {
      httpHeaders = httpHeaders.put(Raw(CaseInsensitiveString(SifHeader.MessageType.toString), sifResponse.messageType.toString))
    }
    if (sifResponse.responseAction.isDefined) {
      httpHeaders = httpHeaders.put(Raw(CaseInsensitiveString(SifHeader.ResponseAction.toString), sifResponse.responseAction.orNull.toString))
    }
    if (sifResponse.requestId.isDefined) {
      httpHeaders = httpHeaders.put(Raw(CaseInsensitiveString(SifHeader.RequestId.toString), sifResponse.requestId.orNull.toString))
    }
    if (sifResponse.serviceType.isDefined) {
      httpHeaders = httpHeaders.put(Raw(CaseInsensitiveString(SifHeader.ServiceType.toString), sifResponse.serviceType.orNull.toString))
    }

    Task.delay(new Response(
      status = httpStatus,
      httpVersion = HttpVersion.`HTTP/1.1`,
      headers = httpHeaders,
      body = sifResponse.getBody(srxRequest.accepts).toEntityBody,
      attributes = AttributeMap.empty
    ))
  }

  def hasError = sifResponse.error.isDefined

  private def getHttpContentType: `Content-Type` = {
    `Content-Type`.parse(srxRequest.accepts.toString).getOrElse(null)
  }
}
