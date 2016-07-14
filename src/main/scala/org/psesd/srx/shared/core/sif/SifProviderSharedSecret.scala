package org.psesd.srx.shared.core.sif

/** Represents a SIF provider shared secret.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class SifProviderSharedSecret(secret: String) {
  override def toString: String = {
    secret
  }
}

object SifProviderSharedSecret {
  def apply(secret: String): SifProviderSharedSecret = new SifProviderSharedSecret(secret)
}
