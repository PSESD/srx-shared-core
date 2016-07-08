package org.psesd.srx.shared.core.sif

import java.util.UUID

/** Represents a SIF-compliant MessageId value.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
case class SifMessageId(id: UUID) {
  override def toString: String = {
    id.toString
  }
}

object SifMessageId {
  def apply(): SifMessageId = new SifMessageId(new UUID(0, 0))
  def apply(id: String): SifMessageId = new SifMessageId(getMessageId(id))

  private def getMessageId(id: String) = {
    UUID.fromString(id)
  }

  def isValid(id: String): Boolean = {
    if (id == null || id.isEmpty) {
      false
    } else {
      try {
        val check = UUID.fromString(id)
        true
      } catch {
        case _: Throwable => false
      }
    }
  }
}
