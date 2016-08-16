package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** SIF request parameter.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifRequestParameter(val key: String, val value: String) {
  if(key.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("key parameter")
  }
}

object SifRequestParameter {
  def apply(key: String, value: String): SifRequestParameter = new SifRequestParameter(key, value)
}