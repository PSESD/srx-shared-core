package org.psesd.srx.shared.core.sif

import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.FunSuite

class SifTimestampTests extends FunSuite {

  private val sampleDateTimeString = "2016-01-01T10:00:00.000-08:00"
  private val sampleDateTime = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(sampleDateTimeString)

  test("new timestamp (defaults to Now)") {
    val timestamp = SifTimestamp()
    assert(timestamp.toString.length > 0)
  }

  test("new timestamp from string") {
    val timestamp = SifTimestamp(sampleDateTimeString)
    val expected = "2016-01-01T18:00:00.000Z"
    val actual = timestamp.toString
    assert(actual.equals(expected))
  }

  test("new timestamp from datetime object") {
    val timestamp = SifTimestamp(sampleDateTime)
    val expected = "2016-01-01T18:00:00.000Z"
    val actual = timestamp.toString
    assert(actual.equals(expected))
  }

}
