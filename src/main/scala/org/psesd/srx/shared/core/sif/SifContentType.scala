package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of SIF accept header values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifContentType extends ExtendedEnumeration {
  type SifContentType = Value
  val Json = Value("application/json")
  val Xml = Value("application/xml")
}
