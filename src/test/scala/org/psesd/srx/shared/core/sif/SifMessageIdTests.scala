package org.psesd.srx.shared.core.sif

import java.util.UUID

import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ExceptionMessage}
import org.scalatest.FunSuite

class SifMessageIdTests extends FunSuite {

  test("default message id") {
    val sifMessageId = SifMessageId()
    assert(sifMessageId.id.toString.length.equals(36))
  }

  test("message id from string") {
    val expected = "ad53dbf6-e0a0-469f-8428-c17738eba43e"
    val sifMessageId = SifMessageId(expected)
    assert(sifMessageId.id.toString.equals(expected))
  }

  test("message id from UUID") {
    val expected = UUID.randomUUID
    val sifMessageId = SifMessageId(expected)
    assert(sifMessageId.id.toString.equals(expected.toString))
  }

  test("message id invalid") {
    val thrown = intercept[ArgumentInvalidException] {
      SifMessageId("1234")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("id parameter value '1234'")))
  }

  test("message id isValid true") {
    val value = "ad53dbf6-e0a0-469f-8428-c17738eba43e"
    assert(SifMessageId.isValid(value))
  }

  test("message id isValid false") {
    val value = "abcd"
    assert(!SifMessageId.isValid(value))
  }

}
