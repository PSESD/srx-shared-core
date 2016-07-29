package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class SifUriTests extends FunSuite {

  test("valid uri") {
    val sifUri = SifUri("https://host/service/object;zoneId=ZONE;contextId=CONTEXT")
    assert(sifUri.contextId.getOrElse("").equals("CONTEXT"))
    assert(sifUri.host.equals("host"))
    assert(sifUri.scheme.equals("https"))
    assert(sifUri.service.getOrElse("").equals("service"))
    assert(sifUri.serviceObject.getOrElse("").equals("object"))
    assert(sifUri.zoneId.getOrElse("").equals("ZONE"))
  }

  test("null uri") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SifUri(null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sifUri")))
  }

  test("empty uri") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SifUri("")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sifUri")))
  }

  test("whitespace uri") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SifUri(" ")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sifUri")))
  }

  test("invalid uri") {
    val thrown = intercept[ArgumentInvalidException] {
      SifUri("not_a_valid_uri")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("sifUri")))
  }

}
