package org.psesd.srx.shared.core.config

import org.psesd.srx.shared.core.exceptions.EnvironmentException
import org.scalatest.FunSuite

class EnvironmentTests extends FunSuite {

  test("environment name is not empty") {
    assert(!Environment.name.isEmpty)
  }

  test("environment default property") {
    assert(Environment.getPropertyOrElse("NOT_A_PROPERTY", "DEFAULT_VALUE") == "DEFAULT_VALUE")
  }

  test("environment invalid property") {
    val thrown = intercept[EnvironmentException] {
      Environment.getProperty("NOT_A_PROPERTY")
    }
    val expected = "Missing environment variable 'NOT_A_PROPERTY'."
    assert(thrown.getMessage.equals(expected))
  }

}
