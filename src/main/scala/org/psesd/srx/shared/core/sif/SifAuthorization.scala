package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, SifAuthenticationMethodInvalidException}

/** Represents a SIF authorization value.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifAuthorization(val provider: SifProvider, val timestamp: SifTimestamp) {
  if (provider == null) {
    throw new ArgumentNullException("provider parameter")
  }
  if (timestamp == null) {
    throw new ArgumentNullException("timestamp parameter")
  }

  val authorizationValue: String = {
    // 1. Concatenate the application ID and date/time and separate them by a ":"
    // 2. Calculate the HMAC SHA 256 value using the unsent Client Application shared secret, and then Base64 encode
    // 3. Combine the applicationKey with this string and separate them by a ":"
    // 4. Base64 encode the result and prefix with the authentication method and a space.

    var sifAuthorizationHash: String = ""

    provider.authenticationMethod match {
      case SifAuthenticationMethod.Basic =>
        sifAuthorizationHash = SifEncryptor.getSifAuthorizationBasicHash(provider, timestamp)

      case SifAuthenticationMethod.SifHmacSha256 =>
        sifAuthorizationHash = SifEncryptor.getSifAuthorizationHmacSha256Hash(provider, timestamp)

      case _ =>
        throw new SifAuthenticationMethodInvalidException(provider.authenticationMethod.toString)
    }

    val combined = provider.sessionToken + ":" + sifAuthorizationHash
    provider.authenticationMethod.toString + " " + SifEncryptor.encodeBasic(combined)
  }

  override def toString: String = {
    authorizationValue
  }

}
