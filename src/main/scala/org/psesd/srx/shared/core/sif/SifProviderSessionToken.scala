package org.psesd.srx.shared.core.sif

/** Represents a SIF provider session token.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class SifProviderSessionToken(token: String) {
  override def toString: String = {
    token
  }
}

object SifProviderSessionToken {
  def apply(token: String): SifProviderSessionToken = new SifProviderSessionToken(token)
}
