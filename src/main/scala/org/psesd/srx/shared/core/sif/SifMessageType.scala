package org.psesd.srx.shared.core.sif

/** Enumeration of SIF messageType header values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifMessageType extends Enumeration {
  type SifMessageType = Value
  val Error = Value("ERROR")
  val Event = Value("EVENT")
  val Request = Value("REQUEST")
  val Response = Value("RESPONSE")

  def withNameCaseInsensitive(s: String): Value = values.find(_.toString.toLowerCase == s.toLowerCase).orNull
}
