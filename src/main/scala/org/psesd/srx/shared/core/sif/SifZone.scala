package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Represents a SIF zone.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class SifZone(zoneId: String) {
  if (zoneId.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("zoneId parameter")
  }

  override def toString: String = {
    zoneId
  }
}

object SifZone {
  final val Default = "default"

  def apply(): SifZone = new SifZone(Default)

  def apply(zoneId: String): SifZone = new SifZone(zoneId)
}
