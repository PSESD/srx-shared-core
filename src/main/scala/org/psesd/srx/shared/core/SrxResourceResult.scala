package org.psesd.srx.shared.core

import scala.collection.mutable.ArrayBuffer
import scala.xml.Node

/** SRX resource result interface.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
trait SrxResourceResult {
  val exceptions = new ArrayBuffer[Exception]()
  val success: Boolean = exceptions.isEmpty

  def toXml: Option[Node]
}