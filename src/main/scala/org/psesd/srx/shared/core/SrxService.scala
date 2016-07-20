package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Represents SRX service.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxService(val name: String, val build: String) {
  if (name.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("name parameter")
  }
  if (build.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("build parameter")
  }
}
