package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class SrxServiceTests extends FunSuite {

  val name: String = "test"
  val build: String = "1.0.1"

  test("valid service") {

    val service = new SrxService(name, build)
    assert(service.name.equals(name))
    assert(service.build.equals(build))
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"$key name causes ArgumentNullOrEmptyOrWhitespaceException") {

      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        new SrxService(value, build)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("name parameter")))
    }

    test(s"$key build causes ArgumentNullOrEmptyOrWhitespaceException") {

      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        new SrxService(name, value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("build parameter")))
    }

  }

}
