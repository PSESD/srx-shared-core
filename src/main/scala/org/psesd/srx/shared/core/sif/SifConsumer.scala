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
import scala.collection.mutable.ArrayBuffer
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
    var httpResponse: CloseableHttpResponse = null
    try {
      httpResponse = httpclient.execute(httpPost)
      response = getSifResponse(sifRequest, httpResponse)
    } catch {
      case e: Exception =>
        response = getErrorResponse(sifRequest, e)
    } finally {
      if(httpResponse != null) {
        httpResponse.close()
      }
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
    var httpResponse: CloseableHttpResponse = null
    try {
      httpResponse = httpclient.execute(httpDelete)
      response = getSifResponse(sifRequest, httpResponse)
    } catch {
      case e: Exception =>
        response = getErrorResponse(sifRequest, e)
    } finally {
      if(httpResponse != null) {
        httpResponse.close()
      }
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
    var httpResponse: CloseableHttpResponse = null
    try {
      httpResponse = httpclient.execute(httpGet)
      response = getSifResponse(sifRequest, httpResponse)
    } catch {
      case e: Exception =>
        response = getErrorResponse(sifRequest, e)
    } finally {
      if(httpResponse != null) {
        httpResponse.close()
      }
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
    var httpResponse: CloseableHttpResponse = null
    try {
      httpResponse = httpclient.execute(httpPut)
      response = getSifResponse(sifRequest, httpResponse)
    } catch {
      case e: Exception =>
        response = getErrorResponse(sifRequest, e)
    } finally {
      if(httpResponse != null) {
        httpResponse.close()
      }
    }
    response
  }

  private def getErrorResponse(sifRequest: SifRequest, exception: Exception): SifResponse = {
    val response = new SifResponse(SifTimestamp(), SifMessageId(), SifMessageType.Response, sifRequest)
    response.exceptions += exception
    response
  }

  private def getSifResponse(sifRequest: SifRequest, httpResponse: CloseableHttpResponse): SifResponse = {

    // maintain a collection of exceptions while processing, but do not fail to construct and return a response
    val exceptions = new ArrayBuffer[Exception]()

    // alert the response processor (after response has been constructed) if invalid Content-Type header was received while processing raw headers
    var invalidContentType = false

    // maintain a set of header variables used for later response construction
    var responseContentType: SifContentType = null
    var responseMessageId: SifMessageId = null
    var responseMessageType: SifMessageType = null
    var responseTimestamp: SifTimestamp = null

    // header key names for headers requiring special validation/processing during initial raw header loop
    val KeyContentType = SifHttpHeader.ContentType.toString.toLowerCase
    val KeyMessageId = SifHeader.MessageId.toString.toLowerCase
    val KeyMessageType = SifHeader.MessageType.toString.toLowerCase
    val KeyTimestamp = SifHeader.Timestamp.toString.toLowerCase

    // must temporarily store header map because a first pass through raw values is required to extract headers needed to construct response
    val headers = new TrieMap[String, String]

    // map all headers, and attempt to find SIF Response headers in the collection
    for (header <- httpResponse.getAllHeaders) {

      val name = header.getName
      val value = header.getValue

      // look for specific headers requiring pre-processing or extraction
      name.toLowerCase match {

        case KeyContentType =>
          // use startsWith on received values due to some providers sending extra Content-Type parameters in the value, such as UTF encoding
          if (value.toLowerCase.startsWith(SifContentType.Xml.toString.toLowerCase)) {
            responseContentType = SifContentType.Xml
          }
          if (value.toLowerCase.startsWith(SifContentType.Json.toString.toLowerCase)) {
            responseContentType = SifContentType.Json
          }
          // if received Content-Type is not supported (not XML or JSON) add an exception to the response (some providers are returning HTML when errors occur on their end)
          if (responseContentType == null) {
            invalidContentType = true
            exceptions += new SifContentTypeInvalidException("Response contains invalid %s: '%s'.".format(SifHttpHeader.ContentType.toString, value))
          }

        case KeyMessageId =>
          responseMessageId = SifMessageId(value)

        case KeyMessageType =>
          responseMessageType = SifMessageType.withNameCaseInsensitive(value)

        case KeyTimestamp =>
          responseTimestamp = SifTimestamp(value)

        case _ =>
      }

      // add header to temp map, to be added to response headers collection later
      headers.putIfAbsent(name, value)
    }

    // if required headers were not found, set implicit defaults
    if (responseMessageId == null) {
      // this should be provided in all responses per SIF spec, but this is not the case with all current providers
      responseMessageId = SifMessageId()
    }
    if (responseMessageType == null) {
      responseMessageType = SifMessageType.Response
    }
    if (responseTimestamp == null) {
      responseTimestamp = SifTimestamp()
    }

    // construct SIF response, populate from raw HTTP response (status, headers, body) and validate
    val response = new SifResponse(responseTimestamp, responseMessageId, responseMessageType, sifRequest)
    response.statusCode = httpResponse.getStatusLine.getStatusCode

    for (header <- headers) {
      response.addHeader(header._1, header._2)
    }

    setContentType(response, responseContentType, invalidContentType)
    response.body = Option(EntityUtils.toString(httpResponse.getEntity))

    for (exception <- exceptions) {
      response.exceptions += exception
    }

    validateResponse(response)

    response
  }

  private def setContentType(response: SifResponse, responseContentType: SifContentType, invalidContentType: Boolean): Unit = {
    if (responseContentType != null) {
      response.contentType = Option(responseContentType)
    } else {
      // if invalid Content-Type header was received, ensure response.contentType is set to None (defaults to XML otherwise).
      if (invalidContentType) {
        response.contentType = None
      }
    }
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
          if (sifResponse.body.orNull != null && !sifResponse.body.get.isEmpty) {
            XML.loadString(sifResponse.body.get)
          }
        } catch {
          case _: Throwable =>
            sifResponse.exceptions += new SifContentTypeInvalidException("Response %s set to '%s' but body does not contain valid XML.".format(SifHttpHeader.ContentType.toString, sifResponse.contentType.get.toString))
        }

      case SifContentType.Json =>
        try {
          if (sifResponse.body.orNull != null && !sifResponse.body.get.isEmpty) {
            parse(sifResponse.body.get)
          }
        } catch {
          case _: Throwable =>
            sifResponse.exceptions += new SifContentTypeInvalidException("Response %s set to '%s' but body does not contain valid JSON.".format(SifHttpHeader.ContentType.toString, sifResponse.contentType.get.toString))
        }

      case _ =>
    }
    sifResponse.exceptions.isEmpty
  }

  private def validateResponse(sifResponse: SifResponse): Boolean = {
    validateContentType(sifResponse)
  }

}

object SifConsumer {
  def apply(): SifConsumer = new SifConsumer()
}
