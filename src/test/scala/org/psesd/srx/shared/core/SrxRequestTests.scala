package org.psesd.srx.shared.core

import org.http4s.Header.Raw
import org.http4s._
import org.http4s.util.CaseInsensitiveString
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullException, ExceptionMessage}
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
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("sifUri parameter")))
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

  test("valid request") {
    val httpRequest = new Request(
      method = Method.GET,
      new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString)
      )
    )
    val srxRequest = SrxRequest(TestValues.sifProvider, httpRequest)
    assert(srxRequest.destination.equals("test"))
    assert(srxRequest.errorMessage.equals(""))
    assert(srxRequest.errorStackTrace.equals(""))
    assert(srxRequest.method.equals("QUERY"))
    assert(srxRequest.source.equals("None"))
    assert(srxRequest.sourceIp.equals("None"))
    assert(srxRequest.userAgent.equals("None"))
    assert(srxRequest.sifRequest.zone.toString.equals("test"))
    assert(srxRequest.sifRequest.context.toString.equals("test"))
    assert(!srxRequest.sifRequest.authorization.toString.isEmpty)
    assert(srxRequest.sifRequest.requestAction.orNull.toString.equals(SifRequestAction.Query.toString))
    assert(srxRequest.sifRequest.uri.toString.equals(testSrxUri.toString))
    assert(srxRequest.sifRequest.timestamp.toString.equals(TestValues.timestamp.toString))
    assert(!srxRequest.acceptsJson)
  }

  test("accepts Json") {
    val httpRequest = new Request(
      method = Method.GET,
      new Uri(None, None, testSrxUri.toString),
      headers = Headers(
        Raw(CaseInsensitiveString(SifHeader.Authorization.toString), TestValues.authorization.toString),
        Raw(CaseInsensitiveString(SifHeader.Timestamp.toString), TestValues.timestamp.toString),
        Raw(CaseInsensitiveString(SifHeader.Accept.toString), SifContentType.Json.toString)
      )
    )
    val srxRequest = SrxRequest(TestValues.sifProvider, httpRequest)
    assert(srxRequest.acceptsJson)
  }

}
