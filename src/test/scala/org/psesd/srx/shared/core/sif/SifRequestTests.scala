package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class SifRequestTests extends FunSuite {

  val authorization = new SifAuthorization(SifTestValues.sifProvider, SifTestValues.timestamp, SifAuthenticationMethod.SifHmacSha256)
  val timestamp = SifTimestamp()

  test("default request") {
    val sifRequest = new SifRequest(authorization, timestamp)

    // constructor
    assert(sifRequest.authorization.toString.equals(authorization.toString))
    assert(sifRequest.timestamp.toString.equals(timestamp.toString))

    // base class
    assert(sifRequest.requestId.isEmpty)
    assert(sifRequest.serviceType.orNull.equals(SifServiceType.Object))

    // request-specific
    assert(sifRequest.accept.orNull.equals(SifAccept.Xml))
    assert(sifRequest.generatorId.isEmpty)
    assert(sifRequest.messageId.isEmpty)
    assert(sifRequest.messageType.orNull.equals(SifMessageType.Request))
    assert(sifRequest.requestAction.isEmpty)
    assert(sifRequest.requestType.orNull.equals(SifRequestType.Immediate))
  }


  test("fully constructed request") {
    val requestId = "1234"
    val serviceType = SifServiceType.Functional
    val accept = SifAccept.Json
    val generatorId = "5678"
    val messageId = SifMessageId("ad53dbf6-e0a0-469f-8428-c17738eba43e")
    val messageType = SifMessageType.Event
    val requestAction = SifRequestAction.Update
    val requestType = SifRequestType.Delayed
    val sifRequest = new SifRequest(authorization, timestamp)
    sifRequest.requestId = Option(requestId)
    sifRequest.serviceType = Option(serviceType)
    sifRequest.accept = Option(accept)
    sifRequest.generatorId = Option(generatorId)
    sifRequest.messageId = Option(messageId)
    sifRequest.messageType = Option(messageType)
    sifRequest.requestAction = Option(requestAction)
    sifRequest.requestType = Option(requestType)

    // constructor
    assert(sifRequest.authorization.toString.equals(authorization.toString))
    assert(sifRequest.timestamp.toString.equals(timestamp.toString))

    // base class
    assert(sifRequest.requestId.orNull.equals(requestId))
    assert(sifRequest.serviceType.orNull.equals(SifServiceType.Functional))

    // request-specific
    assert(sifRequest.accept.orNull.equals(accept))
    assert(sifRequest.generatorId.orNull.equals(generatorId))
    assert(sifRequest.messageId.orNull.equals(messageId))
    assert(sifRequest.messageType.orNull.equals(messageType))
    assert(sifRequest.requestAction.orNull.equals(requestAction))
    assert(sifRequest.requestType.orNull.equals(requestType))
  }

  test("null authorization") {
    val thrown = intercept[ArgumentNullException] {
      new SifRequest(null, timestamp)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("authorization parameter")))
  }

  test("null timestamp") {
    val thrown = intercept[ArgumentNullException] {
      new SifRequest(authorization, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("timestamp parameter")))
  }

}
