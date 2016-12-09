package org.psesd.srx.shared.core

import org.json4s.JValue
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, EnvironmentException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.{SifContentType, SifEncryptor, SifHeader}
import org.psesd.srx.shared.core.sif.SifContentType.SifContentType

import scala.xml.Node

/** SRX request body.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SrxRequestBody(srxRequest: SrxRequest) {
  if (srxRequest == null) {
    throw new ArgumentNullException("srxRequest parameter")
  }
  private val value: Option[String] = srxRequest.sifRequest.body
  private val iv: Option[String] = srxRequest.sifRequest.getHeader(SifHeader.Iv.toString)

  val contentType: Option[SifContentType] = srxRequest.sifRequest.contentType

  def getJson: Option[JValue] = {
    if(value.isDefined && !value.get.isNullOrEmpty) {
      try {
        if(contentType.isDefined && contentType.get.equals(SifContentType.Json)) {
          Some(getValue.get.toJson)
        } else {
          Some(getValue.get.toXml.toJsonString.toJson)
        }
      } catch {
        case ee: EnvironmentException =>
          throw ee
        case ane: ArgumentNullOrEmptyOrWhitespaceException =>
          throw ane
        case _: Exception =>
          throw new ArgumentInvalidException("request body")
      }
    } else {
      None
    }
  }

  def getXml: Option[Node] = {
    if(value.isDefined && !value.get.isNullOrEmpty) {
      try {
        if(contentType.isDefined && contentType.get.equals(SifContentType.Json)) {
          Some(getJson.get.toXml)
        } else {
          Some(getValue.get.toXml)
        }
      } catch {
        case ee: EnvironmentException =>
          throw ee
        case ane: ArgumentNullOrEmptyOrWhitespaceException =>
          throw ane
        case _: Exception =>
          throw new ArgumentInvalidException("request body")
      }
    } else {
      None
    }
  }

  def getValue: Option[String] = {
    if(value.isDefined && !value.get.isNullOrEmpty) {
      try {
        if (value.get.startsWith("<") || value.get.startsWith("{") || value.get.startsWith("[")) {
          Some(value.get)
        } else {
          Some(decryptBody(value))
        }
      } catch {
        case ee: EnvironmentException =>
          throw ee
        case ane: ArgumentNullOrEmptyOrWhitespaceException =>
          throw ane
        case _: Exception =>
          throw new ArgumentInvalidException("request body")
      }
    } else {
      None
    }
  }

  private def decryptBody(value: Option[String]): String = {
    if(iv.isEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("%s header".format(SifHeader.Iv.toString))
    }
    SifEncryptor.decryptString(
      Environment.getProperty("AES_PASSWORD").toCharArray,
      Environment.getProperty("AES_SALT").getBytes,
      value.get,
      iv.get.getBytes
    )
  }

}
