package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.sif.SifRequestParameter

/** SRX resource service interface.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
trait SrxResourceService {
  def delete(parameters: List[SifRequestParameter]): SrxResourceResult

  def create(resource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult

  def query(parameters: List[SifRequestParameter]): SrxResourceResult

  def update(resource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult

  def getIdFromRequestParameters(parameters: List[SifRequestParameter]): Option[String] = {
    getRequestParameter(parameters, "id")
  }

  def getRequestParameter(parameters: List[SifRequestParameter], name: String): Option[String] = {
    if (parameters != null && parameters.nonEmpty) {
      val parameter = parameters.find(p => p.key.toLowerCase == name.toLowerCase()).orNull
      if (parameter != null) {
        Some(parameter.value)
      } else {
        None
      }
    } else {
      None
    }
  }
}
