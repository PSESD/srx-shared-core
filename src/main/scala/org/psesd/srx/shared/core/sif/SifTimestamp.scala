package org.psesd.srx.shared.core.sif

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}

/** Represents a SIF-compliant Date/Time value.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
case class SifTimestamp(dateTime: DateTime) {
  override def toString: String = {
    ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).print(dateTime)
  }
}

object SifTimestamp {
  def apply(): SifTimestamp = new SifTimestamp(DateTime.now(DateTimeZone.UTC))

  def apply(dateTime: String): SifTimestamp = new SifTimestamp(getDateTime(dateTime))

  private def getDateTime(dateTime: String) = {
    ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(dateTime)
  }

  def isValid(dateTime: String): Boolean = {
    if (dateTime == null || dateTime.isEmpty) {
      false
    } else {
      try {
        val check = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(dateTime)
        true
      } catch {
        case _: Throwable => false
      }
    }
  }
}
