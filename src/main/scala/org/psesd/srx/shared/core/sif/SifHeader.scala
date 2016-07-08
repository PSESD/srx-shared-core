package org.psesd.srx.shared.core.sif

/** Enumeration of SIF HTTP headers.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifHeader extends Enumeration {
  type SifHeader = Value
  val Authorization = Value("authorization")
  val GeneratorId = Value("generatorId")
  val MessageId = Value("messageId")
  val MessageType = Value("messageType")
  val RequestAction = Value("requestAction")
  val RequestId = Value("requestId")
  val RequestType = Value("requestType")
  val ResponseAction = Value("responseAction")
  val ServiceType = Value("serviceType")
  val Timestamp = Value("timestamp")
}
