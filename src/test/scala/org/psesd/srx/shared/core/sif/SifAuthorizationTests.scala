package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class SifAuthorizationTests extends FunSuite {

  test("create request authorization Basic") {
    val result = new SifAuthorization(SifTestValues.sifProvider, SifTestValues.timestamp, SifAuthenticationMethod.Basic)
    val expected = SifTestValues.sifAuthorizationBasic
    assert(result.toString.equals(expected))
  }

  test("create request authorization ShaHmac256") {
    val result = new SifAuthorization(SifTestValues.sifProvider, SifTestValues.timestamp, SifAuthenticationMethod.SifHmacSha256)
    val expected = SifTestValues.sifAuthorizationShaHmac256
    assert(result.toString.equals(expected))
  }

  test("create request authorization null provider") {
    val thrown = intercept[ArgumentNullException] {
      new SifAuthorization(null, SifTestValues.timestamp, SifTestValues.sifAuthenticationMethod)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("provider parameter")))
  }

  test("create request authorization null timestamp") {
    val thrown = intercept[ArgumentNullException] {
      new SifAuthorization(SifTestValues.sifProvider, null, SifTestValues.sifAuthenticationMethod)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("timestamp parameter")))
  }

  test("create request authorization null method") {
    val thrown = intercept[ArgumentNullException] {
      new SifAuthorization(SifTestValues.sifProvider, SifTestValues.timestamp, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("method parameter")))
  }

}
