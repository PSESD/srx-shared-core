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
}
