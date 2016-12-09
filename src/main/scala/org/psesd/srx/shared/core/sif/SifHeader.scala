package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of SIF HTTP headers.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifHeader extends ExtendedEnumeration {
  type SifHeader = Value
  val Accept = Value("accept")
  val Authorization = Value("authorization")
  val GeneratorId = Value("generatorId")
  val Iv = Value("x-psesd-iv")
  val MessageId = Value("messageId")
  val MessageType = Value("messageType")
  val QueueId = Value("queueId")
  val RequestAction = Value("requestAction")
  val RequestId = Value("requestId")
  val RequestType = Value("requestType")
  val ResponseAction = Value("responseAction")
  val ServiceType = Value("serviceType")
  val Timestamp = Value("timestamp")

  def isRestricted(name: String): Boolean = {
    name.toLowerCase.equals(Authorization.toString.toLowerCase)
  }
}
