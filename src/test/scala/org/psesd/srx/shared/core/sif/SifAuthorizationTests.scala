package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class SifAuthorizationTests extends FunSuite {

  test("create request authorization Basic") {
    val provider = new SifProvider(SifTestValues.sifUrl, SifTestValues.sessionToken, SifTestValues.sharedSecret, SifAuthenticationMethod.Basic)
    val result = new SifAuthorization(provider, SifTestValues.timestamp).toString
    val expected = SifTestValues.sifAuthorizationBasic
    assert(result.toString.equals(expected))
  }

  test("create request authorization ShaHmac256") {
    val result = new SifAuthorization(SifTestValues.sifProvider, SifTestValues.timestamp).toString
    val expected = SifTestValues.sifAuthorizationShaHmac256
    assert(result.toString.equals(expected))
  }

  test("create request authorization null provider") {
    val thrown = intercept[ArgumentNullException] {
      new SifAuthorization(null, SifTestValues.timestamp)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("provider parameter")))
  }

  test("create request authorization null timestamp") {
    val thrown = intercept[ArgumentNullException] {
      new SifAuthorization(SifTestValues.sifProvider, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("timestamp parameter")))
  }

}
