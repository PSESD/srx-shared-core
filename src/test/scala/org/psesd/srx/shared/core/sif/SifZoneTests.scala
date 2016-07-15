package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class SifZoneTests extends FunSuite {

  test("default zone") {
    val sifZone = SifZone()
    assert(sifZone.toString.equals(SifZone.Default))
  }

  test("explicit zoneId") {
    val sifZone = SifZone("test")
    assert(sifZone.toString.equals("test"))
  }

  test("empty zoneId") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SifZone("")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("zoneId parameter")))
  }

  test("whitespace zoneId") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SifZone("  ")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("zoneId parameter")))
  }

}
