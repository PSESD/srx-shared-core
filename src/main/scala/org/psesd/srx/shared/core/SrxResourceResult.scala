package org.psesd.srx.shared.core

import org.json4s.JValue

import scala.collection.mutable.ArrayBuffer
import scala.xml.Node

/** SRX resource result interface.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
trait SrxResourceResult {
  val exceptions = new ArrayBuffer[Exception]()

  def success: Boolean = exceptions.isEmpty

  var statusCode: Int = 0

  def toJson: Option[JValue]

  def toXml: Option[Node]
}