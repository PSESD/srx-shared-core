package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core._
import org.scalatest.FunSuite

class RollbarClientTests extends FunSuite {

  ignore("test message") {
    val description = "srx-shared-core test message"
    val srxMessage = SrxMessage(TestValues.srxService, description)
    val rollbarMessage = new RollbarMessage(srxMessage, LogLevel.Debug).getJsonString()
    val actual = RollbarClient.SendItem(rollbarMessage)
    val expected = 200
    assert(actual.equals(expected))
  }

}
