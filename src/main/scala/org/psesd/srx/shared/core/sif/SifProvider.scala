package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Represents an authorized SIF provider.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifProvider(val sessionToken: String, val sharedSecret: String) {
  if (sessionToken.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("sessionToken parameter")
  }
  if (!sessionToken.isUuid) {
    throw new ArgumentInvalidException("sessionToken parameter")
  }
  if (sharedSecret.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("sharedSecret parameter")
  }
}
