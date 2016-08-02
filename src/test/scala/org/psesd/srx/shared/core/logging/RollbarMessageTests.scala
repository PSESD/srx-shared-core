package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.psesd.srx.shared.core.{SrxMessage, TestValues}
import org.scalatest.FunSuite

class RollbarMessageTests extends FunSuite {

  test("valid message") {
    val message = new RollbarMessage(SrxMessage(TestValues.srxService, "test"), LogLevel.Error).getJsonString()
    assert(!message.isEmpty)
    assert(message.startsWith("{\"access_token\""))
  }

  test("srxMessage null") {
    val thrown = intercept[ArgumentNullException] {
      new RollbarMessage(null, LogLevel.Error)
    }
    val expected = ExceptionMessage.NotNull.format("srxMessage parameter")
    assert(thrown.getMessage.equals(expected))
  }

  test("logLevel null") {
    val thrown = intercept[ArgumentNullException] {
      new RollbarMessage(SrxMessage(TestValues.srxService, "test"), null)
    }
    val expected = ExceptionMessage.NotNull.format("logLevel parameter")
    assert(thrown.getMessage.equals(expected))
  }

}
