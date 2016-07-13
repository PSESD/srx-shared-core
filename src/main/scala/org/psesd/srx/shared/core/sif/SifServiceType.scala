package org.psesd.srx.shared.core.sif

/** Enumeration of SIF serviceType header values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifServiceType extends Enumeration {
  type SifServiceType = Value
  val Functional = Value("FUNCTIONAL")
  val Object = Value("OBJECT")
  val ServicePath = Value("SERVICEPATH")
  val Utility = Value("UTILITY")
  val XQueryTemplate = Value("XQUERYTEMPLATE")
}
