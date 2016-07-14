package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException

/** Represents a SIF zone.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class SifZone(zoneId: String) {
  if (zoneId == null) {
    throw new ArgumentNullException("zoneId parameter")
  }

  override def toString: String = {
    zoneId
  }
}

object SifZone {
  def apply(): SifZone = new SifZone(Default)
  def apply(zoneId: String): SifZone = new SifZone(zoneId)

  final val Default = "DEFAULT"
}
