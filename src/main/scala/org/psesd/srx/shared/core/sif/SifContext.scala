package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Represents a SIF context.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class SifContext(contextId: String) {
  if (contextId.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("contextId parameter")
  }

  override def toString: String = {
    contextId
  }
}

object SifContext {
  final val Default = "DEFAULT"

  def apply(): SifContext = new SifContext(Default)

  def apply(contextId: String): SifContext = new SifContext(contextId)
}
