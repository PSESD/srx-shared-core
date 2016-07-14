package org.psesd.srx.shared.core.sif

/** Represents a SIF provider URL.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class SifProviderUrl(url: String) {
  override def toString: String = {
    url
  }
}

object SifProviderUrl {
  def apply(url: String): SifProviderUrl = new SifProviderUrl(url)
}
