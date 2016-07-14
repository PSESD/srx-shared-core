package org.psesd.srx.shared.core.sif

import org.apache.http.client.methods._
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif.SifMessageType.SifMessageType

import scala.collection.concurrent.TrieMap

/** Submits SIF requests to Environments Provider.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifConsumer {

  val httpclient: CloseableHttpClient = HttpClients.createDefault()

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
    httpPost.setEntity(new StringEntity(sifRequest.body.orNull))

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
    httpPut.setEntity(new StringEntity(sifRequest.body.orNull))

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
    var responseMessageId: SifMessageId = null
    var responseMessageType: SifMessageType = null
    var responseTimestamp: SifTimestamp = null
    val KeyMessageId = SifHeader.MessageId.toString.toLowerCase
    val KeyMessageType = SifHeader.MessageType.toString.toLowerCase
    val KeyTimestamp = SifHeader.Timestamp.toString.toLowerCase

    // map all headers, and attempt to find SIF Response headers in the collection
    val headers = new TrieMap[String, String]
    for (header <- httpResponse.getAllHeaders) {
      val name = header.getName
      val value = header.getValue
      name.toLowerCase match {
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
    response
  }

  private def setHttpHeaders(sifRequest: SifRequest, httpRequest: HttpRequestBase): Unit = {
    for (header <- sifRequest.getHeaders) {
      httpRequest.setHeader(header._1, header._2)
    }
  }

}

object SifConsumer {
  def apply(): SifConsumer = new SifConsumer()
}
