package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration
import org.psesd.srx.shared.core.sif.SifRequestParameter

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

  def getResponseFormat(parameters: List[SifRequestParameter]): SrxResponseFormat = {
    // default to SIF-compliant response
    var format: SrxResponseFormat = Sif

    // if a ResponseFormat parameter was received, attempt to parse and use it instead
    if(parameters != null && parameters.nonEmpty) {
      val formatParam = parameters.find(p => p.key.toLowerCase() == "responseformat")
      if(formatParam.isDefined) {
        val formatOption = SrxResponseFormat.withNameCaseInsensitiveOption(formatParam.get.value)
        if(formatOption.isDefined) {
          format = formatOption.get
        }
      }
    }

    format
  }
}