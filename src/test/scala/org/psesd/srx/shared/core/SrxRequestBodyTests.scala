package org.psesd.srx.shared.core

import org.http4s.Header.Raw
import org.http4s.util.CaseInsensitiveString
import org.http4s.{Headers, Method, Request, Uri}
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.psesd.srx.shared.core.extensions.HttpTypeExtensions._
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

class SrxRequestBodyTests extends FunSuite {

  lazy val testSrxUri = new SifUri(TestValues.sifProvider.url + "/test_service/test_resource/test_resource_id;zoneId=test;contextId=test")
  lazy val testIv = "e675f725e675f725"
  lazy val testPassword = Environment.getProperty("AES_PASSWORD").toCharArray
  lazy val testSalt = Environment.getProperty("AES_SALT").getBytes
  lazy val testBodyValue = "test body"
  lazy val testBodyXml = <xml>{testBodyValue}</xml>.toString
  lazy val testBodyXmlEncrypted = SifEncryptor.encryptString(testPassword, testSalt, testBodyXml, testIv.getBytes)
  lazy val testBodyXmlInvalid = "<not>invalid</valid>"
  lazy val testBodyXmlEncryptedInvalid = SifEncryptor.encryptString(testPassword, testSalt, testBodyXmlInvalid, testIv.getBytes)
  lazy val testBodyJson = "{\"body\": \"test body\"}"
  lazy val testBodyJsonEncrypted = SifEncryptor.encryptString(testPassword, testSalt, testBodyJson, testIv.getBytes)
  lazy val testBodyJsonInvalid = "{\"not: \"valid\""
  lazy val testBodyJsonEncryptedInvalid = SifEncryptor.encryptString(testPassword, testSalt, testBodyJsonInvalid, testIv.getBytes)

  test("null srxRequest") {
    val thrown = intercept[ArgumentNullException] {
      new SrxRequestBody(null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("srxRequest parameter")))
  }

  test("invalid xml") {
    val srxRequest = getRequest(testBodyXmlInvalid, testIv, null)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val thrown = intercept[ArgumentInvalidException] {
      srxRequestBody.getXml.get
    }
    assert(thrown.getMessage.equals("The request body is invalid."))
  }

  test("invalid encrypted xml") {
    val srxRequest = getRequest(testBodyXmlEncryptedInvalid, testIv, null)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val thrown = intercept[ArgumentInvalidException] {
      srxRequestBody.getXml.get
    }
    assert(thrown.getMessage.equals("The request body is invalid."))
  }

  test("invalid json") {
    val srxRequest = getRequest(testBodyJsonInvalid, testIv, SifContentType.Json.toString)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val thrown = intercept[ArgumentInvalidException] {
      srxRequestBody.getJson.get
    }
    assert(thrown.getMessage.equals("The request body is invalid."))
  }

  test("invalid encrypted json") {
    val srxRequest = getRequest(testBodyJsonEncryptedInvalid, testIv, SifContentType.Json.toString)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val thrown = intercept[ArgumentInvalidException] {
      srxRequestBody.getJson.get
    }
    assert(thrown.getMessage.equals("The request body is invalid."))
  }

  test("missing iv") {
    val srxRequest = getRequest(testBodyXmlEncrypted, null, null)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      srxRequestBody.getXml.get
    }
    assert(thrown.getMessage.equals("The x-psesd-iv header cannot be null, empty, or whitespace."))
  }

  test("invalid iv") {
    val srxRequest = getRequest(testBodyXmlEncrypted, "not-a-valid-iv", null)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val thrown = intercept[ArgumentInvalidException] {
      srxRequestBody.getXml.get
    }
    assert(thrown.getMessage.equals("The request body is invalid."))
  }

  test("valid xml") {
    val srxRequest = getRequest(testBodyXml, testIv, null)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val bodyXml = srxRequestBody.getXml.get
    assert(bodyXml.text.equals(testBodyValue))
  }

  test("valid encrypted xml") {
    val srxRequest = getRequest(testBodyXmlEncrypted, testIv, null)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val bodyXml = srxRequestBody.getXml.get
    assert(bodyXml.text.equals(testBodyValue))
  }

  test("valid xml to json") {
    val srxRequest = getRequest(testBodyXml, testIv, null)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val bodyJson = srxRequestBody.getJson.get
    assert(bodyJson.toJsonString.contains(testBodyValue))
  }

  test("valid encrypted xml to json") {
    val srxRequest = getRequest(testBodyXmlEncrypted, testIv, null)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val bodyJson = srxRequestBody.getXml.get
    assert(bodyJson.toJsonString.contains(testBodyValue))
  }

  test("valid json") {
    val srxRequest = getRequest(testBodyJson, testIv, SifContentType.Json.toString)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val bodyJson = srxRequestBody.getJson.get
    assert(bodyJson.toJsonString.contains(testBodyValue))
  }

  test("valid encrypted json") {
    val srxRequest = getRequest(testBodyJsonEncrypted, testIv, SifContentType.Json.toString)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val bodyJson = srxRequestBody.getJson.get
    assert(bodyJson.toJsonString.contains(testBodyValue))
  }

  test("valid json to xml") {
    val srxRequest = getRequest(testBodyJson, testIv, SifContentType.Json.toString)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val bodyXml = srxRequestBody.getXml.get
    assert(bodyXml.text.equals(testBodyValue))
  }

  test("valid encrypted json to xml") {
    val srxRequest = getRequest(testBodyJsonEncrypted, testIv, SifContentType.Json.toString)
    val srxRequestBody = new SrxRequestBody(srxRequest)
    val bodyXml = srxRequestBody.getXml.get
    assert(bodyXml.text.equals(testBodyValue))
  }

  private def getRequest(requestBody: String, iv: String, contentType: String): SrxRequest = {
    SrxRequest(
      TestValues.sifProvider,
      new Request(
        method = Method.GET,
        new Uri(None, None, testSrxUri.toString),
        headers = Headers(
          Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
          Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
          Raw(CaseInsensitiveString(SifHeader.Iv.toString), iv),
          Raw(CaseInsensitiveString(SifHttpHeader.ContentType.toString), contentType)
        ),
        body = requestBody.toEntityBody
      )
    )
  }

}
