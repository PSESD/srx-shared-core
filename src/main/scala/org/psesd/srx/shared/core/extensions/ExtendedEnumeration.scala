package org.psesd.srx.shared.core.extensions

/** Extension for Scala base Enumeration class.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class ExtendedEnumeration extends Enumeration {
  def withNameCaseInsensitive(s: String): Value = values.find(_.toString.toLowerCase == s.toLowerCase).orNull
}
