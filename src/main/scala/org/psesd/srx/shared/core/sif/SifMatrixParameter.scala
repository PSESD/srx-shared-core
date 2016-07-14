package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of SIF matrix parameters.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifMatrixParameter extends ExtendedEnumeration {
  type SifMatrixParameter = Value
  val ContextId = Value("contextId")
  val ZoneId = Value("zoneId")
}