package org.psesd.srx.shared.core.sif

import java.util.UUID

import org.psesd.srx.shared.core.exceptions.ArgumentInvalidException

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
  def apply(): SifMessageId = new SifMessageId(UUID.randomUUID)
  def apply(id: String): SifMessageId = new SifMessageId(getMessageId(id))

  private def getMessageId(id: String) = {
    if(!isValid(id)) {
      throw new ArgumentInvalidException("id parameter value '%s'".format(id))
    }
    UUID.fromString(id)
  }

  def isValid(id: String): Boolean = {
    if (id == null || id.isEmpty) {
      false
    } else {
      try {
        UUID.fromString(id)
        true
      } catch {
        case _: Throwable => false
      }
    }
  }
}
