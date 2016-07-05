package org.psesd.srx.shared.core.logging

import org.scalatest.FunSuite

class LoggerTests extends FunSuite {

  test("log debug string") {
    Logger.log(LogLevel.Debug, "Test Debug log string.")
  }

  test("log info string") {
    Logger.log(LogLevel.Info, "Test Info log string.")
  }

  test("log warning string") {
    Logger.log(LogLevel.Warning, "Test Warning log string.")
  }

  test("log error string") {
    Logger.log(LogLevel.Error, "Test Error log string.")
  }

}
