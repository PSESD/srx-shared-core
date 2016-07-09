package org.psesd.srx.shared.core.sif

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64
import org.joda.time._
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Provides SIF authentication methods.
  *
  * @version 1.0
  * @since 1.0
  * @author David S. Dennison (iTrellis, LLC)
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object SifAuthenticator {

  def getSifHash(username: String, timestamp: String, secret: Array[Byte]): String = {

    if (username.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("username parameter")
    }

    if (timestamp.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("timestamp parameter")
    }

    if (secret.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("secret parameter")
    }

    try {
      DateTime.parse(timestamp.trim)
    } catch {
      case _: Throwable => throw new ArgumentInvalidException("timestamp")
    }

    val plaintext = s"${username.trim}:${timestamp.trim}".getBytes

    val spec = "HmacSHA256"
    val mac = Mac.getInstance(spec)
    val secretKey = new SecretKeySpec(secret, spec)
    mac.init(secretKey)

    Base64.encodeBase64String(mac.doFinal(plaintext))
  }

}
