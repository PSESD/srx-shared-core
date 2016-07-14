package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of SIF requestType header values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifRequestType extends ExtendedEnumeration {
  type SifRequestType = Value
  val Delayed = Value("DELAYED")
  val Immediate = Value("IMMEDIATE")
}
