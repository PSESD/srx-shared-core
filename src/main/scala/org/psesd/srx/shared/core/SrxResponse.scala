package org.psesd.srx.shared.core

import org.http4s.Header.Raw
import org.http4s.util.CaseInsensitiveString
import org.http4s._
import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif._

import scalaz.concurrent.Task

/** Represents outgoing SRX response.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxResponse (val srxRequest: SrxRequest) {
  if (srxRequest == null) {
    throw new ArgumentNullException("srxRequest parameter")
  }

  val sifResponse = new SifResponse(SifTimestamp(), SifMessageId(), SifMessageType.Response, srxRequest.sifRequest)

  def hasError = sifResponse.error.isDefined

  def setError(sifError: SifError): Unit = {
    sifResponse.statusCode = sifError.code
    sifResponse.error = Option(sifError)
  }

  def toHttpResponse: Task[Response] = {
    val httpStatus = Status.fromInt(sifResponse.statusCode).valueOr(null)
    val httpHeaders = Headers(
      Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), sifResponse.timestamp.toString)
    )

    new Response(status = httpStatus, headers = httpHeaders).withBody(sifResponse.getBody(srxRequest.accepts))
  }
}
