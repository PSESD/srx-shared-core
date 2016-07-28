package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core._
import org.psesd.srx.shared.core.sif._
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
    val sifRequest = new SifRequest(SifTestValues.sifProvider, "", SifZone(), SifContext(), SifTestValues.timestamp)
    val srxRequest = SrxRequest(sifRequest)
    val srxMessage = SrxMessage(
      Option(messageId),
      timestamp,
      TestValues.srxService,
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
