package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core.{SrxMessage, SrxOperation, SrxOperationStatus, SrxRequest}
import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.psesd.srx.shared.core.sif.{SifMessageId, SifTimestamp}
import org.scalatest.FunSuite

class LoggerTests extends FunSuite {

  test("null level string") {
    val thrown = intercept[ArgumentNullException] {
      Logger.log(null, "Test local log string.")
    }
    val expected = ExceptionMessage.NotNull.format("level parameter")
    assert(thrown.getMessage.equals(expected))
  }

  test("null message string") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      Logger.log(LogLevel.Local, "")
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("message parameter")
    assert(thrown.getMessage.equals(expected))
  }

  test("null level srxMessage") {
    val thrown = intercept[ArgumentNullException] {
      Logger.log(null, SrxMessage.getEmpty)
    }
    val expected = ExceptionMessage.NotNull.format("level parameter")
    assert(thrown.getMessage.equals(expected))
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
    Logger.log(LogLevel.Info, message)
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
    val srxRequestMessageId = srxRequest.messageId
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
