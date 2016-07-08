package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyOrWhitespaceException}
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
      val sifUri = SifUri(null)
    }
    val expected = "The URI value cannot be null, empty, or whitespace."
    assert(thrown.getMessage.equals(expected))
  }

  test("empty uri") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val sifUri = SifUri("")
    }
    val expected = "The URI value cannot be null, empty, or whitespace."
    assert(thrown.getMessage.equals(expected))
  }

  test("whitespace uri") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val sifUri = SifUri(" ")
    }
    val expected = "The URI value cannot be null, empty, or whitespace."
    assert(thrown.getMessage.equals(expected))
  }

  test("invalid uri") {
    val thrown = intercept[ArgumentInvalidException] {
      val sifUri = SifUri("not_a_valid_uri")
    }
    val expected = "The URI value is invalid."
    assert(thrown.getMessage.equals(expected))
  }

}
