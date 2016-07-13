package org.psesd.srx.shared.core.sif

/** Enumeration of SIF accept header values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifAccept extends Enumeration {
  type SifAccept = Value
  val Json = Value("application/json")
  val Xml = Value("application/xml")
}
