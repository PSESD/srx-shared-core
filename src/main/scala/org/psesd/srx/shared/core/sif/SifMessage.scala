package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif.SifContentType.SifContentType
import org.psesd.srx.shared.core.sif.SifServiceType.SifServiceType

import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArrayBuffer

/** Represents a SIF message.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifMessage(val timestamp: SifTimestamp) {
  if (timestamp == null) {
    throw new ArgumentNullException("timestamp parameter")
  }

  protected val headers = new TrieMap[String, String]

  var contentType: Option[SifContentType] = Option(SifContentType.Xml)
  var body: Option[String] = None
  val exceptions = new ArrayBuffer[Exception]()
  var requestId: Option[String] = None
  var serviceType: Option[SifServiceType] = Option(SifServiceType.Object)

  def addHeader(key: String, value: String): Unit = {
    if (key != null && !key.isEmpty && value != null && !value.isEmpty) {
      headers.putIfAbsent(key, value)
    }
  }

  def isValid: Boolean = {
    exceptions.isEmpty
  }
}
