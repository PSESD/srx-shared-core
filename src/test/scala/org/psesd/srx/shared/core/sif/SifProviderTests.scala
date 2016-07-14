package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class SifProviderTests extends FunSuite {

  test("valid provider") {
    val sifProvider = new SifProvider(SifTestValues.sifUrl, SifTestValues.sessionToken, SifTestValues.sharedSecret, SifTestValues.sifAuthenticationMethod)
    assert(sifProvider.sessionToken.equals(SifTestValues.sessionToken))
    assert(sifProvider.sharedSecret.equals(SifTestValues.sharedSecret))
  }

  test("null url") {
    val thrown = intercept[ArgumentNullException] {
      new SifProvider(null, SifTestValues.sessionToken, SifTestValues.sharedSecret, SifTestValues.sifAuthenticationMethod)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("url parameter")))
  }

  test("null sessionToken") {
    val thrown = intercept[ArgumentNullException] {
      new SifProvider(SifTestValues.sifUrl, null, SifTestValues.sharedSecret, SifTestValues.sifAuthenticationMethod)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("sessionToken parameter")))
  }

  test("null sharedSecret") {
    val thrown = intercept[ArgumentNullException] {
      new SifProvider(SifTestValues.sifUrl, SifTestValues.sessionToken, null, SifTestValues.sifAuthenticationMethod)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("sharedSecret parameter")))
  }

  test("null authenticationMethod") {
    val thrown = intercept[ArgumentNullException] {
      new SifProvider(SifTestValues.sifUrl, SifTestValues.sessionToken, SifTestValues.sharedSecret, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("authenticationMethod parameter")))
  }

}
