package org.psesd.srx.shared.core

import org.http4s._
import org.psesd.srx.shared.core.SrxOperation.SrxOperation
import org.psesd.srx.shared.core.sif.{SifHeader, SifHttpHeader, SifMessageId, SifUri}

import scala.collection.mutable

/** Represents incoming SRX requests.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxRequest private(val key: Int, val operation: SrxOperation, val studentId: String) {

  val headers = mutable.Map[String, String]()
  val messageId = SifMessageId()
  var method = ""
  var body = ""
  var destination = ""
  var errorMessage = ""
  var errorStackTrace = ""
  var requestId = ""
  var source = ""
  var sourceIp = ""
  var uri = ""
  var userAgent = ""

  def this(key: Int, request: Request, operation: SrxOperation, studentId: String) = {
    this(key, operation, studentId)

    if (request != null) {
      body = request.body.toString // TODO: Confirm this works/encodes correctly!
      destination = getDestination(request.uri.toString)
      for (h <- request.headers) {
        headers += h.name.value -> h.value
      }
      method = request.method.toString
      requestId = request.headers.find(x => x.name.value.toLowerCase == SifHeader.RequestId.toString.toLowerCase).fold("")(_.value)
      source = getSource(request.headers)
      sourceIp = getSourceIp(request.headers)
      uri = request.uri.toString
      userAgent = getUserAgent(request.headers)
    }
  }

  /** Gets the destination from the request headers if found; otherwise, an empty string.
    *
    * @param uri the request URI.
    * */
  private def getDestination(uri: String): String = {
    SifUri(uri).zoneId.getOrElse("")
  }

  /** Gets the source from the request headers if found; otherwise, ''None''.
    *
    * @param headers the request headers.
    * */
  private def getSource(headers: Headers): String = {
    val h: Option[Header] = headers.find(x => x.name.value.toLowerCase == SifHeader.GeneratorId.toString.toLowerCase)
    h.fold("None")(_.value)
  }

  /** Gets the client source IP from the request headers if found; otherwise, ''None''.
    *
    * @param headers the request headers.
    * */
  private def getSourceIp(headers: Headers): String = {
    val h: Option[Header] = headers.find(x => x.name.value.toLowerCase == SifHttpHeader.ForwardedFor.toString.toLowerCase)
    h.fold("None")(_.value)
  }

  /** Gets the client user agent from the request headers if found; otherwise, ''None''.
    *
    * @param headers the request headers.
    * */
  private def getUserAgent(headers: Headers): String = {
    val h: Option[Header] = headers.find(x => x.name.value.toLowerCase == SifHttpHeader.UserAgent.toString.toLowerCase)
    h.fold("None")(_.value)
  }
}

