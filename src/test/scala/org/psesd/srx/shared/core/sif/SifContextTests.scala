package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class SifContextTests extends FunSuite {

  test("default context") {
    val sifContext = SifContext()
    assert(sifContext.toString.equals(SifContext.Default))
  }

  test("explicit contextId") {
    val sifContext = SifContext("test")
    assert(sifContext.toString.equals("test"))
  }

  test("empty contextId") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SifContext("")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("contextId parameter")))
  }

  test("whitespace contextId") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SifContext("  ")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("contextId parameter")))
  }

}
