package org.psesd.srx.shared.core.sif

import org.apache.http.client.methods._
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.json4s.native.JsonMethods._
import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, SifContentTypeInvalidException}
import org.psesd.srx.shared.core.sif.SifContentType.SifContentType
import org.psesd.srx.shared.core.sif.SifMessageType.SifMessageType

import scala.collection.concurrent.TrieMap
import scala.xml.XML

/** Submits SIF requests to Environments Provider.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifConsumer {

  val httpclient: CloseableHttpClient = HttpClients.custom().disableCookieManagement().build()

  def create(sifRequest: SifRequest): SifResponse = {
    if (sifRequest == null) {
      throw new ArgumentNullException("sifRequest parameter")
    }
    if (sifRequest.body.orNull == null) {
      throw new ArgumentNullException("sifRequest body")
    }

    sifRequest.requestAction = Option(SifRequestAction.Create)

    val httpPost = new HttpPost(sifRequest.uri.toString)
    setHttpHeaders(sifRequest, httpPost)
    httpPost.setEntity(new StringEntity(sifRequest.body.get))

    var response: SifResponse = null
    val httpResponse = httpclient.execute(httpPost)
    try {
      response = getSifResponse(sifRequest, httpResponse)
    } finally {
      httpResponse.close()
    }
    response
  }

  def delete(sifRequest: SifRequest): SifResponse = {
    if (sifRequest == null) {
      throw new ArgumentNullException("sifRequest parameter")
    }

    sifRequest.requestAction = Option(SifRequestAction.Delete)

    val httpDelete = new HttpDelete(sifRequest.uri.toString)
    setHttpHeaders(sifRequest, httpDelete)

    var response: SifResponse = null
    val httpResponse = httpclient.execute(httpDelete)
    try {
      response = getSifResponse(sifRequest, httpResponse)
    } finally {
      httpResponse.close()
    }
    response
  }

  def query(sifRequest: SifRequest): SifResponse = {
    if (sifRequest == null) {
      throw new ArgumentNullException("sifRequest parameter")
    }

    sifRequest.requestAction = Option(SifRequestAction.Query)

    val httpGet = new HttpGet(sifRequest.uri.toString)
    setHttpHeaders(sifRequest, httpGet)

    var response: SifResponse = null
    val httpResponse = httpclient.execute(httpGet)
    try {
      response = getSifResponse(sifRequest, httpResponse)
    } finally {
      httpResponse.close()
    }
    response
  }

  def update(sifRequest: SifRequest): SifResponse = {
    if (sifRequest == null) {
      throw new ArgumentNullException("sifRequest parameter")
    }
    if (sifRequest.body.orNull == null) {
      throw new ArgumentNullException("sifRequest body")
    }

    sifRequest.requestAction = Option(SifRequestAction.Update)

    val httpPut = new HttpPut(sifRequest.uri.toString)
    setHttpHeaders(sifRequest, httpPut)
    httpPut.setEntity(new StringEntity(sifRequest.body.get))

    var response: SifResponse = null
    val httpResponse = httpclient.execute(httpPut)
    try {
      response = getSifResponse(sifRequest, httpResponse)
    } finally {
      httpResponse.close()
    }
    response
  }

  private def getSifResponse(sifRequest: SifRequest, httpResponse: CloseableHttpResponse): SifResponse = {
    var responseContentType: SifContentType = null
    var responseMessageId: SifMessageId = null
    var responseMessageType: SifMessageType = null
    var responseTimestamp: SifTimestamp = null
    val KeyContentType = SifHttpHeader.ContentType.toString.toLowerCase
    val KeyMessageId = SifHeader.MessageId.toString.toLowerCase
    val KeyMessageType = SifHeader.MessageType.toString.toLowerCase
    val KeyTimestamp = SifHeader.Timestamp.toString.toLowerCase

    // map all headers, and attempt to find SIF Response headers in the collection
    val headers = new TrieMap[String, String]
    for (header <- httpResponse.getAllHeaders) {
      val name = header.getName
      val value = header.getValue
      name.toLowerCase match {
        case KeyContentType =>
          if(value.toLowerCase.startsWith(SifContentType.Xml.toString.toLowerCase)) {
            responseContentType = SifContentType.Xml
          }
          if(value.toLowerCase.startsWith(SifContentType.Json.toString.toLowerCase)) {
            responseContentType = SifContentType.Json
          }
          if(responseContentType == null) {
            throw new SifContentTypeInvalidException("Response contains invalid %s: '%s'.".format(SifHttpHeader.ContentType.toString, value))
          }

        case KeyMessageId =>
          responseMessageId = SifMessageId(value)

        case KeyMessageType =>
          responseMessageType = SifMessageType.withNameCaseInsensitive(value)

        case KeyTimestamp =>
          responseTimestamp = SifTimestamp(value)

        case _ =>
      }
      headers.putIfAbsent(name, value)
    }

    // if required headers were not found, set implicit defaults
    if (responseContentType == null) {
      responseContentType = SifContentType.Xml
    }
    if (responseMessageId == null) {
      responseMessageId = SifMessageId()
    }
    if (responseMessageType == null) {
      responseMessageType = SifMessageType.Response
    }
    if (responseTimestamp == null) {
      responseTimestamp = SifTimestamp()
    }

    // construct response and populate from HTTP response (status, headers, body)
    val response = new SifResponse(responseTimestamp, responseMessageId, responseMessageType, sifRequest)
    response.statusCode = httpResponse.getStatusLine.getStatusCode
    for (header <- headers) {
      response.addHeader(header._1, header._2)
    }
    response.body = Option(EntityUtils.toString(httpResponse.getEntity))
    validateResponse(response)
    response
  }

  private def setHttpHeaders(sifRequest: SifRequest, httpRequest: HttpRequestBase): Unit = {
    for (header <- sifRequest.getHeaders) {
      httpRequest.setHeader(header._1, header._2)
    }
  }

  private def validateContentType(sifResponse: SifResponse): Boolean = {
    sifResponse.contentType.orNull match {
      case SifContentType.Xml =>
        try {
          if(sifResponse.body.orNull != null && !sifResponse.body.get.isEmpty) {
            XML.loadString(sifResponse.body.get)
          }
        } catch {
          case _: Throwable =>
            throw new SifContentTypeInvalidException("Response %s set to '%s' but body does not contain valid XML.".format(SifHttpHeader.ContentType.toString, sifResponse.contentType.get.toString))
        }

      case SifContentType.Json =>
        try {
          if(sifResponse.body.orNull != null && !sifResponse.body.get.isEmpty) {
            parse(sifResponse.body.get)
          }
        } catch {
          case _: Throwable =>
            throw new SifContentTypeInvalidException("Response %s set to '%s' but body does not contain valid JSON.".format(SifHttpHeader.ContentType.toString, sifResponse.contentType.get.toString))
        }

      case _ =>
    }
    true
  }

  private def validateResponse(sifResponse: SifResponse): Boolean = {
    validateContentType(sifResponse)
  }

}

object SifConsumer {
  def apply(): SifConsumer = new SifConsumer()
}
