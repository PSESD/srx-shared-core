package org.psesd.srx.shared.core.sif

import org.json4s._
import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.SifContentType.SifContentType
import org.psesd.srx.shared.core.sif.SifMessageType.SifMessageType

import scala.collection.concurrent.TrieMap
import scala.xml.Node

/** Represents a SIF response.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifResponse(timestamp: SifTimestamp,
                  val messageId: SifMessageId,
                  val messageType: SifMessageType,
                  val sifRequest: SifRequest) extends SifMessage(timestamp) {
  if (timestamp == null) {
    throw new ArgumentNullException("timestamp parameter")
  }
  if (messageId == null) {
    throw new ArgumentNullException("messageId parameter")
  }
  if (messageType == null) {
    throw new ArgumentNullException("messageType parameter")
  }

  val responseAction = {
    if (sifRequest == null) {
      None
    } else {
      sifRequest.requestAction.orElse(None)
    }
  }

  requestId = {
    if (sifRequest == null) {
      None
    } else {
      sifRequest.requestId.orElse(None)
    }
  }

  serviceType = {
    if (sifRequest == null) {
      None
    } else {
      sifRequest.serviceType.orElse(None)
    }
  }

  var bodyXml: Option[Node] = None
  var error: Option[SifError] = None
  var statusCode: Int = 0

  def getBody(contentType: SifContentType): String = {
    contentType match {

      case SifContentType.Json =>
        if (error.isDefined) {
          error.get.toXml.toJsonString
        } else {
          if (bodyXml.isDefined) {
            bodyXml.get.toJsonString
          } else {
            if (body.isDefined) {
              if(body.get.isJson) {
                body.get
              } else {
                if(body.get.isXml) {
                  body.get.toXml.toJsonString
                } else {
                  ""
                }
              }
            } else {
              ""
            }
          }
        }
      case SifContentType.Xml =>
        if (error.isDefined) {
          error.get.toXml.toXmlString
        } else {
          if (bodyXml.isDefined) {
            bodyXml.get.toXmlString
          } else {
            if (body.isDefined) {
              if(body.get.isXml) {
                body.get
              } else {
                if(body.get.isJson) {
                  body.get.toJson.toXml.toString
                } else {
                  ""
                }
              }
            } else {
              ""
            }
          }
        }
    }
  }

  def getBodyJson: Option[JValue] = {
    val jsonString = getBody(SifContentType.Json)
    if(jsonString.isNullOrEmpty) {
      None
    } else {
      Some(jsonString.toJson)
    }
  }

  def getBodyXml: Option[Node] = {
    val xmlString = getBody(SifContentType.Xml)
    if(xmlString.isNullOrEmpty) {
      None
    } else {
      Some(xmlString.toXml)
    }
  }

  def getHeaders: TrieMap[String, String] = {
    addHeader(SifHttpHeader.ContentType.toString, contentType.getOrElse("").toString)
    addHeader(SifHeader.MessageId.toString, messageId.toString)
    addHeader(SifHeader.MessageType.toString, messageType.toString)
    addHeader(SifHeader.ResponseAction.toString, responseAction.getOrElse("").toString)
    addHeader(SifHeader.RequestId.toString, requestId.getOrElse(""))
    addHeader(SifHeader.ServiceType.toString, serviceType.getOrElse("").toString)
    addHeader(SifHeader.Timestamp.toString, timestamp.toString)
    headers
  }
}
