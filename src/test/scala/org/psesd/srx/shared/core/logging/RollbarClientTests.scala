package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core.{SrxMessage, SrxOperation, SrxOperationStatus, SrxRequest}
import org.psesd.srx.shared.core.sif.{SifMessageId, SifTimestamp}
import org.scalatest.FunSuite

class RollbarClientTests extends FunSuite {

  ignore("test message") {
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

    val rollbarMessage = new RollbarMessage(srxMessage, LogLevel.Debug).getJsonString()
    val actual = RollbarClient.SendItem(rollbarMessage)
    val expected = 200
    assert(actual.equals(expected))
  }

}
