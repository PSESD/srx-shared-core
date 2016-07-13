package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions._
import org.psesd.srx.shared.core.sif.SifAuthenticationMethod.SifAuthenticationMethod
import org.scalatest.FunSuite

class SifAuthenticatorTests extends FunSuite {

  test("create authenticator") {
    new SifAuthenticator(SifTestValues.sifProviders, SifTestValues.sifAuthenticationMethods)
  }

  test("create authenticator null providers") {
    val thrown = intercept[ArgumentNullOrEmptyException] {
      new SifAuthenticator(null, SifTestValues.sifAuthenticationMethods)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("providers parameter")))
  }

  test("create authenticator empty providers") {
    val thrown = intercept[ArgumentNullOrEmptyException] {
      new SifAuthenticator(List[SifProvider](), SifTestValues.sifAuthenticationMethods)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("providers parameter")))
  }

  test("create authenticator null methods") {
    val thrown = intercept[ArgumentNullOrEmptyException] {
      new SifAuthenticator(SifTestValues.sifProviders, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("methods parameter")))
  }

  test("create authenticator empty methods") {
    val thrown = intercept[ArgumentNullOrEmptyException] {
      new SifAuthenticator(SifTestValues.sifProviders, List[SifAuthenticationMethod]())
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("methods parameter")))
  }

  test("validate request authorization Basic") {
    val authenticator = new SifAuthenticator(SifTestValues.sifProviders, List[SifAuthenticationMethod](SifAuthenticationMethod.Basic))
    val result = authenticator.validateRequestAuthorization(SifTestValues.sifAuthorizationBasic, SifTestValues.timestamp.toString)
    val expected = true
    assert(result.equals(expected))
  }

  test("validate request authorization ShaHmac256") {
    val authenticator = new SifAuthenticator(SifTestValues.sifProviders, SifTestValues.sifAuthenticationMethods)
    val result = authenticator.validateRequestAuthorization(SifTestValues.sifAuthorizationShaHmac256, SifTestValues.timestamp.toString)
    val expected = true
    assert(result.equals(expected))
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"validate request authorization $key authorization value") {
      val authenticator = new SifAuthenticator(SifTestValues.sifProviders, SifTestValues.sifAuthenticationMethods)
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        authenticator.validateRequestAuthorization(value, SifTestValues.timestamp.toString)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("authorization parameter")))
    }

    test(s"validate request authorization $key timestamp value") {
      val authenticator = new SifAuthenticator(SifTestValues.sifProviders, SifTestValues.sifAuthenticationMethods)
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        authenticator.validateRequestAuthorization(SifTestValues.sifAuthorizationShaHmac256, value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("timestamp parameter")))
    }
  }

  test("validate request authorization invalid timestamp value") {
    val authenticator = new SifAuthenticator(SifTestValues.sifProviders, SifTestValues.sifAuthenticationMethods)
    val thrown = intercept[ArgumentInvalidException] {
      authenticator.validateRequestAuthorization(SifTestValues.sifAuthorizationShaHmac256, "1234")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("dateTime parameter value '1234'")))
  }

  test("validate request authorization invalid authorization value") {
    val authenticator = new SifAuthenticator(SifTestValues.sifProviders, SifTestValues.sifAuthenticationMethods)
    val thrown = intercept[ArgumentInvalidException] {
      authenticator.validateRequestAuthorization("1234", SifTestValues.timestamp.toString)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("authorization parameter")))
  }

  test("validate request authorization method not supported") {
    val authenticator = new SifAuthenticator(SifTestValues.sifProviders, SifTestValues.sifAuthenticationMethods)
    val authorization = new SifAuthorization(SifTestValues.sifProvider, SifTestValues.timestamp, SifAuthenticationMethod.Basic)
    val thrown = intercept[SifAuthenticationMethodInvalidException] {
      authenticator.validateRequestAuthorization(authorization.toString, SifTestValues.timestamp.toString)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.SifAuthenticationMethodInvalid.format(SifAuthenticationMethod.Basic.toString)))
  }

  test("validate request authorization provider not authorized") {
    val invalidSessionToken = "23832cf1-a1cb-41e3-b358-1f9cbb9646c6"
    val authenticator = new SifAuthenticator(SifTestValues.sifProviders, SifTestValues.sifAuthenticationMethods)
    val authorization = new SifAuthorization(new SifProvider(invalidSessionToken, "456"), SifTestValues.timestamp, SifTestValues.sifAuthenticationMethod)
    val thrown = intercept[SifProviderNotAuthorizedException] {
      authenticator.validateRequestAuthorization(authorization.toString, SifTestValues.timestamp.toString)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.SifProviderNotAuthorized.format(invalidSessionToken)))
  }

  test("validate request authorization invalid authorization hash") {
    val combined = SifTestValues.sessionToken + ":" + "invalid_hash"
    val authorization = SifAuthenticationMethod.Basic.toString + " " + SifEncryptor.encodeBasic(combined)
    val authenticator = new SifAuthenticator(SifTestValues.sifProviders, List[SifAuthenticationMethod](SifAuthenticationMethod.Basic))
    val thrown = intercept[ArgumentInvalidException] {
      authenticator.validateRequestAuthorization(authorization, SifTestValues.timestamp.toString)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("authorization parameter")))
  }

}
