package org.psesd.srx.shared.core.sif

/** Enumeration of SIF requestType header values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifRequestType extends Enumeration {
  type SifRequestType = Value
  val Delayed = Value("DELAYED")
  val Immediate = Value("IMMEDIATE")
}
