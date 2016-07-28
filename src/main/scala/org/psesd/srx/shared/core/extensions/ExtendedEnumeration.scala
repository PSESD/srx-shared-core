package org.psesd.srx.shared.core.extensions

/** Extension for Scala base Enumeration class.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class ExtendedEnumeration extends Enumeration {

  def withNameCaseInsensitive(s: String): Value = {
    if(s == null || s.isEmpty) {
      null
    } else {
      values.find(_.toString.toLowerCase == s.toLowerCase).orNull
    }
  }

  def withNameCaseInsensitiveOption(s: String): Option[Value] = {
    if(s == null || s.isEmpty) {
      None
    } else {
      val value = values.find(_.toString.toLowerCase == s.toLowerCase).orNull
      if(value == null) {
        None
      } else {
        Option(value)
      }
    }
  }

}
