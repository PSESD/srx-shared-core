package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyException}

/** Represents SRX service.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxService(val service: SrxServiceComponent, val buildComponents: List[SrxServiceComponent]) {
  if (service == null) {
    throw new ArgumentNullException("service parameter")
  }
  if (buildComponents == null || buildComponents.isEmpty) {
    throw new ArgumentNullOrEmptyException("buildComponents parameter")
  }
}
