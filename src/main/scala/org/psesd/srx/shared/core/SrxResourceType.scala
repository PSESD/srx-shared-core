package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of supported SRX resource types.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SrxResourceType extends ExtendedEnumeration {
  type SrxResourceType = Value
  val Filters = Value("filters")
  val MasterXsres = Value("masterXsres")
  val Sres = Value("sres")
  val SrxMessages = Value("srxMessages")
  val Xsres = Value("xSres")
  val ZoneConfig = Value("srxZoneConfig")
}
