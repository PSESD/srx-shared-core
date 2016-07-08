package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core.sif.{SifMessageId, SifTimestamp}
import org.psesd.srx.shared.core.{SrxMessage, SrxOperation, SrxOperationStatus, SrxRequest}
import org.scalatest.FunSuite

class LoggerTests extends FunSuite {

  test("null level string") {
    Logger.log(null, "Test local log string.")
  }

  test("null message string") {
    Logger.log(LogLevel.Local, "")
  }

  test("null level srxMessage") {
    Logger.log(null, SrxMessage.getEmpty)
  }

  test("log local string") {
    Logger.log(LogLevel.Local, "Test local log string.")
  }

  test("log local message") {
    val message = SrxMessage(
      Option(SifMessageId()),
      SifTimestamp(),
      None,
      None,
      None,
      None,
      Option("Test local log message."),
      None,
      None,
      None,
      None
    )
    Logger.log(LogLevel.Local, message)
  }

  ignore("log debug message") {
    val messageId = SifMessageId()
    val timestamp = SifTimestamp()
    val operation = SrxOperation.None
    val status = SrxOperationStatus.None
    val source = "source"
    val destination = "destination"
    val description = "srx-shared-core test message"
    val body = "body"
    val sourceIp = "sourceIp"
    val userAgent = "userAgent"
    val srxRequest = new SrxRequest(0, null, null, null)
    val srxMessage = SrxMessage(
      Option(messageId),
      timestamp,
      Option(operation),
      Option(status),
      Option(source),
      Option(destination),
      Option(description),
      Option(body),
      Option(sourceIp),
      Option(userAgent),
      Option(srxRequest)
    )
    Logger.log(LogLevel.Debug, srxMessage)
  }

}
