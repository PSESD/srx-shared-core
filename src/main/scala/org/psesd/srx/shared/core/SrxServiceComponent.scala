package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Represents SRX service component.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxServiceComponent(val name: String, val version: String) {
  if (name.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("name parameter")
  }
  if (version.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("version parameter")
  }
}
