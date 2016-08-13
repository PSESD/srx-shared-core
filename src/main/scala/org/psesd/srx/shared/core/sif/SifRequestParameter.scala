package org.psesd.srx.shared.core.sif

/** SIF request parameter.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifRequestParameter(val key: String, val value: String) {
}

object SifRequestParameter {
  def apply(key: String, value: String): SifRequestParameter = new SifRequestParameter(key, value)
}