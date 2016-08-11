package org.psesd.srx.shared.core

import org.http4s.Header.Raw
import org.http4s._
import org.http4s.util.CaseInsensitiveString
import org.psesd.srx.shared.core.exceptions._
import org.psesd.srx.shared.core.extensions.HttpTypeExtensions._
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

class SrxRequestTests extends FunSuite {

  val testSrxUri = new SifUri(TestValues.sifProvider.url + "/test_service/test_resource/test_resource_id;zoneId=test;contextId=test")

  test("empty request") {
    val sifRequest = new SifRequest(TestValues.sifProvider, "", SifZone(), SifContext(), TestValues.timestamp)
    val request = SrxRequest(sifRequest)
    assert(request.sifRequest.body.getOrElse("") == "")
  }

  test("null sifRequest") {
    val thrown = intercept[ArgumentNullException] {
      SrxRequest(null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("sifRequest parameter")))
  }

  test("null provider") {
    val thrown = intercept[ArgumentNullException] {
      SrxRequest(null, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("provider parameter")))
  }

  test("null httpRequest") {
    val thrown = intercept[ArgumentNullException] {
      SrxRequest(TestValues.sifProvider, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("httpRequest parameter")))
  }

  test("invalid sifUri") {
    val httpRequest = new Request
    val thrown = intercept[ArgumentInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("sifUri")))
  }

  test("null authorization header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString)
    )
    val thrown = intercept[ArgumentNullException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("authorization header")))
  }

  test("invalid authorization header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), "invalid"),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString)
      )
    )
    val thrown = intercept[SifRequestNotAuthorizedException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("authorization header")))
  }

  test("null timestamp header") {
    val httpRequest = new Request(
      method = Method.GET,
      new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString)
      )
    )
    val thrown = intercept[ArgumentNullException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("timestamp header")))
  }

  test("invalid timestamp header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), "invalid")
      )
    )
    val thrown = intercept[ArgumentInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("timestamp header")))
  }

  test("invalid accept header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.Accept.toString), "invalid")
      )
    )
    val thrown = intercept[SifHeaderInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.SifHeaderInvalid.format(SifHeader.Accept.toString, "invalid")))
  }

  test("invalid content-type header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHttpHeader.ContentType.toString), "invalid")
      )
    )
    val thrown = intercept[SifHeaderInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.SifHeaderInvalid.format(SifHttpHeader.ContentType.toString, "invalid")))
  }

  test("invalid messageType header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.MessageType.toString), "invalid")
      )
    )
    val thrown = intercept[SifHeaderInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.SifHeaderInvalid.format(SifHeader.MessageType.toString, "invalid")))
  }

  test("invalid requestAction header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.RequestAction.toString), "invalid")
      )
    )
    val thrown = intercept[SifHeaderInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.SifHeaderInvalid.format(SifHeader.RequestAction.toString, "invalid")))
  }

  test("invalid requestType header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.RequestType.toString), "invalid")
      )
    )
    val thrown = intercept[SifHeaderInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.SifHeaderInvalid.format(SifHeader.RequestType.toString, "invalid")))
  }

  test("invalid serviceType header") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.ServiceType.toString), "invalid")
      )
    )
    val thrown = intercept[SifHeaderInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.SifHeaderInvalid.format(SifHeader.ServiceType.toString, "invalid")))
  }

  test("empty CREATE body") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.RequestAction.toString), SifRequestAction.Create.toString)
      )
    )
    val thrown = intercept[ArgumentInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("request body")))
  }

  test("empty UPDATE body") {
    val httpRequest = new Request(
      method = Method.GET,
      uri = new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.RequestAction.toString), SifRequestAction.Update.toString)
      )
    )
    val thrown = intercept[ArgumentInvalidException] {
      SrxRequest(TestValues.sifProvider, httpRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("request body")))
  }

  test("valid xml request") {
    val requestBody = "<xml>test body</xml>"
    val httpRequest = new Request(
      method = Method.GET,
      new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString)
      ),
      body = requestBody.toEntityBody
    )
    val srxRequest = SrxRequest(TestValues.sifProvider, httpRequest)
    assert(srxRequest.errorMessage.isEmpty)
    assert(srxRequest.errorStackTrace.isEmpty)
    assert(srxRequest.method.equals("QUERY"))
    assert(srxRequest.sifRequest.zone.toString.equals("test"))
    assert(srxRequest.sifRequest.context.toString.equals("test"))
    assert(!srxRequest.sifRequest.authorization.toString.isEmpty)
    assert(srxRequest.sifRequest.requestAction.orNull.toString.equals(SifRequestAction.Query.toString))
    assert(srxRequest.sifRequest.uri.toString.equals(testSrxUri.toString))
    assert(srxRequest.sifRequest.timestamp.toString.equals(TestValues.timestamp.toString))
    assert(!srxRequest.acceptsJson)
    assert(srxRequest.getBodyXml.get.toXmlString.equals(requestBody))
  }

  test("valid json request") {
    val requestBody = "{ \"message\" : { \"messageId\" : \"da9b4078-fdae-4280-bb0c-4feb069c18b6\" } }"
    val bodyJsonLinux = "{\n  \"message\" : {\n    \"messageId\" : \"da9b4078-fdae-4280-bb0c-4feb069c18b6\"\n  }\n}"
    val bodyJsonWindows = "{\r\n  \"message\" : {\r\n    \"messageId\" : \"da9b4078-fdae-4280-bb0c-4feb069c18b6\"\r\n  }\r\n}"
    val httpRequest = new Request(
      method = Method.GET,
      new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.Accept.toString), SifContentType.Json.toString),
        Raw(CaseInsensitiveString(SifHttpHeader.ContentType.toString), "json")
      ),
      body = requestBody.toEntityBody
    )
    val srxRequest = SrxRequest(TestValues.sifProvider, httpRequest)
    assert(srxRequest.acceptsJson)
    assert(srxRequest.sifRequest.contentType.get.equals(SifContentType.Json))
    val bodyJsonString = srxRequest.getBodyXml.get.toJsonString
    assert(bodyJsonString.equals(bodyJsonLinux) || bodyJsonString.equals(bodyJsonWindows))
  }

  test("getBodyXml empty body") {
    val httpRequest = new Request(
      method = Method.GET,
      new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString)
      )
    )
    val srxRequest = SrxRequest(TestValues.sifProvider, httpRequest)
    assert(srxRequest.getBodyXml.isEmpty)
  }


  test("getBodyXml invalid XML body") {
    val requestBody = "{ \"message\" : { \"messageId\" : \"da9b4078-fdae-4280-bb0c-4feb069c18b6\" } }"
    val httpRequest = new Request(
      method = Method.GET,
      new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString)
      ),
      body = requestBody.toEntityBody
    )
    val request = SrxRequest(TestValues.sifProvider, httpRequest)
    val thrown = intercept[ArgumentInvalidException] {
      request.getBodyXml
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("request body XML")))
  }

  test("getBodyXml invalid JSON body") {
    val requestBody = "<xml>test body</xml>"
    val httpRequest = new Request(
      method = Method.GET,
      new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHttpHeader.ContentType.toString), SifContentType.Json.toString)
      ),
      body = requestBody.toEntityBody
    )
    val request = SrxRequest(TestValues.sifProvider, httpRequest)
    val thrown = intercept[ArgumentInvalidException] {
      request.getBodyXml
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("request body JSON")))
  }

}
