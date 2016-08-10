package org.psesd.srx.shared.core.sif

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.psesd.srx.shared.core.exceptions.ArgumentInvalidException

/** Represents a SIF-compliant Date/Time value.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
case class SifTimestamp(dateTime: DateTime) {

  var originalString: Option[String] = None

  override def toString: String = {
    ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).print(dateTime)
  }

  def getMilliseconds: Long = {
    dateTime.getMillis
  }

  def getOriginalString: String = {
    if(originalString.isDefined) {
      originalString.get
    } else {
      toString
    }
  }

}

object SifTimestamp {
  def apply(): SifTimestamp = new SifTimestamp(DateTime.now(DateTimeZone.UTC))

  def apply(dateTime: String): SifTimestamp = {
    val timestamp = new SifTimestamp(getDateTime(dateTime))
    timestamp.originalString = Some(dateTime)
    timestamp
  }

  private def getDateTime(dateTime: String) = {
    if (!isValid(dateTime)) {
      throw new ArgumentInvalidException("dateTime parameter value '%s'".format(dateTime))
    }
    ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(dateTime)
  }

  def isValid(dateTime: String): Boolean = {
    if (dateTime == null || dateTime.isEmpty) {
      false
    } else {
      try {
        ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(dateTime)
        true
      } catch {
        case _: Throwable => false
      }
    }
  }
}
