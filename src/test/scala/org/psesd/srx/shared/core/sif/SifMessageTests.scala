package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class SifMessageTests extends FunSuite {

  val timestamp = SifTimestamp()

  test("default message") {
    val sifMessage = new SifMessage(timestamp)

    assert(sifMessage.timestamp.toString.equals(timestamp.toString))
    assert(sifMessage.requestId.isEmpty)
    assert(sifMessage.serviceType.orNull.equals(SifServiceType.Object))
  }

  test("fully constructed message") {
    val requestId = "1234"
    val serviceType = SifServiceType.Functional
    val sifMessage = new SifMessage(timestamp)
    sifMessage.requestId = Option(requestId)
    sifMessage.serviceType = Option(serviceType)

    assert(sifMessage.timestamp.toString.equals(timestamp.toString))
    assert(sifMessage.requestId.orNull.equals(requestId))
    assert(sifMessage.serviceType.orNull.equals(serviceType))
  }

  test("null timestamp") {
    val thrown = intercept[ArgumentNullException] {
      new SifMessage(null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("timestamp parameter")))
  }

}
