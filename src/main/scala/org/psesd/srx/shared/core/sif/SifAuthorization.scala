package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, SifAuthenticationMethodInvalidException}
import org.psesd.srx.shared.core.sif.SifAuthenticationMethod.SifAuthenticationMethod

/** Represents a SIF authorization value.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifAuthorization(val provider: SifProvider, val timestamp: SifTimestamp, val method: SifAuthenticationMethod) {
  if (provider == null) {
    throw new ArgumentNullException("provider parameter")
  }
  if (timestamp == null) {
    throw new ArgumentNullException("timestamp parameter")
  }
  if (method == null) {
    throw new ArgumentNullException("method parameter")
  }

  val authorizationValue: String = {
    // 1. Concatenate the application ID and date/time and separate them by a ":"
    // 2. Calculate the HMAC SHA 256 value using the unsent Client Application shared secret, and then Base64 encode
    // 3. Combine the applicationKey with this string and separate them by a ":"
    // 4. Base64 encode the result and prefix with the authentication method and a space.

    var sifAuthorizationHash: String = ""

    method match {
      case SifAuthenticationMethod.Basic =>
        sifAuthorizationHash = SifEncryptor.getSifAuthorizationBasicHash(provider, timestamp)

      case SifAuthenticationMethod.SifHmacSha256 =>
        sifAuthorizationHash = SifEncryptor.getSifAuthorizationHmacSha256Hash(provider, timestamp)

      case _ =>
        throw new SifAuthenticationMethodInvalidException(method.toString)
    }

    val combined = provider.sessionToken + ":" + sifAuthorizationHash
    method.toString + " " + SifEncryptor.encodeBasic(combined)
  }

  override def toString: String = {
    authorizationValue
  }

}
