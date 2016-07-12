package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions._
import org.psesd.srx.shared.core.sif.SifAuthenticationMethod.SifAuthenticationMethod
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Provides SIF authentication methods.
  *
  * @version 1.0
  * @since 1.0
  * @author David S. Dennison (iTrellis, LLC)
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifAuthenticator(providers: List[SifProvider], methods: List[SifAuthenticationMethod]) {
  if (providers == null || providers.isEmpty) {
    throw new ArgumentNullOrEmptyException("providers parameter")
  }
  if (methods == null || methods.isEmpty) {
    throw new ArgumentNullOrEmptyException("methods parameter")
  }

  def validateRequestAuthorization(authorization: String, timestamp: String): Boolean = {
    if (authorization.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("authorization parameter")
    }
    if (timestamp.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("timestamp parameter")
    }
    val sifTimestamp = SifTimestamp(timestamp)

    if(!(validateAuthorizationHasTwoValues(authorization) &
      validateAuthorizationEncoding(authorization))) {
      throw new ArgumentInvalidException("authorization parameter")
    }

    val authenticationMethod = getValidAuthenticationMethod(authorization)
    if(authenticationMethod == null) {
      throw new SifAuthenticationMethodInvalidException(authorization.split(' ').head)
    }

    val sessionToken = getAuthorizationSessionToken(authorization)

    val provider = getValidProvider(sessionToken)
    if (provider == null) {
      throw new SifProviderNotAuthorizedException(sessionToken)
    }

    validateAuthorizationSifHash(authorization, sifTimestamp, provider, authenticationMethod)
  }

  private def getAuthorizationEncoding(authorization: String): String = {
    getAuthorizationValue(authorization, ' ', 1)
  }

  private def getAuthorizationHash(authorization: String): String = {
    val authEncoding = getAuthorizationEncoding(authorization)
    val decoded = SifEncryptor.decodeBasic(authEncoding)
    getAuthorizationValue(decoded, ':', 1)
  }

  private def getAuthorizationSessionToken(authorization: String): String = {
    val authEncoding = getAuthorizationEncoding(authorization)
    val decoded = SifEncryptor.decodeBasic(authEncoding)
    getAuthorizationValue(decoded, ':', 0)
  }

  private def getAuthorizationValue(authorization: String, splitChar: Char, index: Int): String = {
    val values = authorization.split(splitChar)
    if (values.length == 2 && index >= 0 && index <= 1) values(index) else ""
  }

  private def getValidAuthenticationMethod(authorization: String): SifAuthenticationMethod = {
    methods.find(m => authorization.toLowerCase.startsWith(m.toString.toLowerCase)).orNull
  }

  private def getValidProvider(sessionToken: String): SifProvider = {
    providers.find(p => p.sessionToken.equals(sessionToken)).orNull
  }

  private def validateAuthorizationEncoding(authorization: String): Boolean = {
    val authEncoding = getAuthorizationEncoding(authorization)
    val decoded = SifEncryptor.decodeBasic(authEncoding)
    decoded.split(':').length == 2
  }

  private def validateAuthorizationHasTwoValues(authorization: String): Boolean = {
    authorization.split(' ').length == 2
  }

  private def validateAuthorizationSifHash(authorization: String, timestamp: SifTimestamp, provider: SifProvider, method: SifAuthenticationMethod): Boolean = {
    val hash = getAuthorizationHash(authorization)
    if(hash.isNullOrEmpty) {
      throw new ArgumentInvalidException("authorization parameter")
    }

    val internalHash = method match {
      case SifAuthenticationMethod.Basic =>
        SifEncryptor.getSifAuthorizationBasicHash(provider, timestamp)

      case SifAuthenticationMethod.SifHmacSha256 =>
        SifEncryptor.getSifAuthorizationHmacSha256Hash(provider, timestamp)

      case _ =>
        throw new ArgumentInvalidException("authorization parameter")
    }
    if(hash.contains(internalHash)) {
      true
    } else {
      throw new ArgumentInvalidException("authorization parameter")
    }
  }

}
