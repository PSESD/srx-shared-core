package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException

/** Represents a SIF context.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class SifContext(contextId: String) {
  if (contextId == null) {
    throw new ArgumentNullException("contextId parameter")
  }

  override def toString: String = {
    contextId
  }
}

object SifContext {
  def apply(): SifContext = new SifContext(Default)
  def apply(contextId: String): SifContext = new SifContext(contextId)

  final val Default = "DEFAULT"
}
