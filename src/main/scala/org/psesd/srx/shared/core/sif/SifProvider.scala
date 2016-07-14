package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif.SifAuthenticationMethod.SifAuthenticationMethod

/** Represents an authorized SIF provider.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifProvider(val url: SifProviderUrl,
                  val sessionToken: SifProviderSessionToken,
                  val sharedSecret: SifProviderSharedSecret,
                  val authenticationMethod: SifAuthenticationMethod) {
  if (url == null) {
    throw new ArgumentNullException("url parameter")
  }
  if (sessionToken == null) {
    throw new ArgumentNullException("sessionToken parameter")
  }
  if (sharedSecret == null) {
    throw new ArgumentNullException("sharedSecret parameter")
  }
  if (authenticationMethod == null) {
    throw new ArgumentNullException("authenticationMethod parameter")
  }
}
