package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class SrxServiceComponentTests extends FunSuite {

  test("valid component") {
    val component = new SrxServiceComponent(TestValues.srxService.service.name, TestValues.srxService.service.version)
    assert(component.name.equals(TestValues.srxService.service.name))
    assert(component.version.equals(TestValues.srxService.service.version))
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"$key name causes ArgumentNullOrEmptyOrWhitespaceException") {

      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        new SrxServiceComponent(value, TestValues.srxService.service.version)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("name parameter")))
    }

    test(s"$key build causes ArgumentNullOrEmptyOrWhitespaceException") {

      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        new SrxServiceComponent(TestValues.srxService.service.name, value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("version parameter")))
    }

  }

}
