package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core._
import org.scalatest.FunSuite

class LoggerTests extends FunSuite {

  test("null level string") {
    Logger.log(null, "Test local log string.", TestValues.srxService)
  }

  test("null message string") {
    Logger.log(LogLevel.Local, "", TestValues.srxService)
  }

  test("null srxMessage") {
    Logger.log(null, null)
  }

  test("log local string") {
    Logger.log(LogLevel.Local, "Test local log string.", TestValues.srxService)
  }

  test("log local message") {
    Logger.log(LogLevel.Local, SrxMessage(TestValues.srxService, "Test local log message."))
  }

  ignore("log debug message") {
    val description = "srx-shared-core test message"
    val srxMessage = SrxMessage(TestValues.srxService, description)
    Logger.log(LogLevel.Debug, srxMessage)
  }

}
