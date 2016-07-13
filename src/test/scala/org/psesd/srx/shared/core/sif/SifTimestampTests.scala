package org.psesd.srx.shared.core.sif

import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ExceptionMessage}
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

  test("invalid timestamp") {
    val thrown = intercept[ArgumentInvalidException] {
      SifTimestamp("1234")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("dateTime parameter value '1234'")))
  }

  test("timestamp isValid true") {
    val value = "2016-01-01T18:00:00.000Z"
    assert(SifTimestamp.isValid(value))
  }

  test("timestamp isValid false") {
    val value = "abcd"
    assert(!SifTimestamp.isValid(value))
  }

}
