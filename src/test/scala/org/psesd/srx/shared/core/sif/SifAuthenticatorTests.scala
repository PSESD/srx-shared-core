package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class SifAuthenticatorTests extends FunSuite {

  val username: String = "JohnDoe"
  val timestamp: String = "2015-02-24T20:51:59.878Z"
  val sharedSecret: Array[Byte] = "pHkAuxdGGMWS".getBytes
  val sifHash: String = "xcvz9HCKBl4Ehl5Y8wD3FGuGcucQpOmR8wcH0ArfbP0="

  test("get SIF hash") {
    val result: String = SifAuthenticator.getSifHash(username, timestamp, sharedSecret)
    assert(result === sifHash)
  }

  Map("null" -> null,
    "empty" -> new Array[Byte](0)).foreach { case (key, value) =>

    test(s"get SIF hash with $key secret causes ArgumentNullOrEmptyException") {
      val thrown = intercept[ArgumentNullOrEmptyException] {
        SifAuthenticator.getSifHash(username, timestamp, value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("secret parameter")))
    }

  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"get SIF hash with $key username causes ArgumentNullOrEmptyOrWhitespaceException") {

      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        SifAuthenticator.getSifHash(value, timestamp, sharedSecret)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("username parameter")))
    }

    test(s"get SIF hash with $key timestamp causes ArgumentNullOrEmptyOrWhitespaceException") {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        SifAuthenticator.getSifHash(username, value, sharedSecret)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("timestamp parameter")))
    }
  }

}
