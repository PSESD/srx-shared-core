package org.psesd.srx.shared.core

import org.json4s.JValue

import scala.xml.Node

/** SRX resource construction error result.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxResourceErrorResult(httpStatusCode: Int, exception: Exception) extends SrxResourceResult {
  exceptions += exception
  statusCode = httpStatusCode

  def toJson: Option[JValue] = None

  def toXml: Option[Node] = None
}

object SrxResourceErrorResult {
  def apply(httpStatusCode: Int, exception: Exception): SrxResourceErrorResult = new SrxResourceErrorResult(httpStatusCode, exception)
}