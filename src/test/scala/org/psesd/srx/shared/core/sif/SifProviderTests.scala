package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class SifProviderTests extends FunSuite {

  test("valid provider") {
    val sifProvider = new SifProvider(SifTestValues.sessionToken, SifTestValues.sharedSecret)
    assert(sifProvider.sessionToken.equals(SifTestValues.sessionToken))
    assert(sifProvider.sharedSecret.equals(SifTestValues.sharedSecret))
  }

  test("null sessionToken") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      new SifProvider(null, SifTestValues.sharedSecret)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sessionToken parameter")))
  }

  test("empty sessionToken") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      new SifProvider("", SifTestValues.sharedSecret)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sessionToken parameter")))
  }

  test("whitespace sessionToken") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      new SifProvider(" ", SifTestValues.sharedSecret)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sessionToken parameter")))
  }

  test("invalid sessionToken") {
    val thrown = intercept[ArgumentInvalidException] {
      new SifProvider("123", SifTestValues.sharedSecret)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("sessionToken parameter")))
  }

  test("null sharedSecret") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      new SifProvider(SifTestValues.sessionToken, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sharedSecret parameter")))
  }

  test("empty sharedSecret") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      new SifProvider(SifTestValues.sessionToken, "")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sharedSecret parameter")))
  }

  test("whitespace sharedSecret") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      new SifProvider(SifTestValues.sessionToken, " ")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sharedSecret parameter")))
  }

}
