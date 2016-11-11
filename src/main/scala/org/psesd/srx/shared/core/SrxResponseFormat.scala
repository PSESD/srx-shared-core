package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of supported SRX response formats.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SrxResponseFormat extends ExtendedEnumeration {
  type SrxResponseFormat = Value
  val Object = Value("Object")
  val Sif = Value("Sif")
}