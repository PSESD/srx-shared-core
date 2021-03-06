package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class SifRequestTests extends FunSuite {

  test("default request") {
    val sifRequest = new SifRequest(SifTestValues.sifProvider, "", SifZone(), SifContext(), SifTestValues.timestamp)
    sifRequest.validateReceivedHeaders()

    // constructor
    assert(sifRequest.authorization.toString.equals(SifTestValues.authorization.toString))
    assert(sifRequest.timestamp.toString.equals(SifTestValues.timestamp.toString))

    // base class
    assert(sifRequest.requestId.isEmpty)
    assert(sifRequest.serviceType.orNull.equals(SifServiceType.Object))

    // request-specific
    assert(sifRequest.accept.orNull.equals(SifContentType.Xml))
    assert(sifRequest.context.toString.equals(SifContext.Default))
    assert(sifRequest.generatorId.isEmpty)
    assert(sifRequest.messageId.isEmpty)
    assert(sifRequest.messageType.orNull.equals(SifMessageType.Request))
    assert(sifRequest.requestAction.isEmpty)
    assert(sifRequest.requestType.orNull.equals(SifRequestType.Immediate))
    assert(sifRequest.zone.toString.equals(SifZone.Default))
  }


  test("fully constructed request") {
    val accept = SifContentType.Json
    val context = SifContext("contextFoo")
    val generatorId = "5678"
    val messageId = SifMessageId("ad53dbf6-e0a0-469f-8428-c17738eba43e")
    val messageType = SifMessageType.Event
    val requestAction = SifRequestAction.Update
    val requestId = "1234"
    val requestType = SifRequestType.Delayed
    val serviceType = SifServiceType.Functional
    val zone = SifZone("zoneFoo")
    val sifRequest = new SifRequest(SifTestValues.sifProvider, "", zone, context, SifTestValues.timestamp)
    sifRequest.requestId = Option(requestId)
    sifRequest.serviceType = Option(serviceType)
    sifRequest.accept = Option(accept)
    sifRequest.generatorId = Option(generatorId)
    sifRequest.messageId = Option(messageId)
    sifRequest.messageType = Option(messageType)
    sifRequest.requestAction = Option(requestAction)
    sifRequest.requestType = Option(requestType)

    // constructor
    assert(sifRequest.authorization.toString.equals(SifTestValues.authorization.toString))
    assert(sifRequest.timestamp.toString.equals(SifTestValues.timestamp.toString))

    // base class
    assert(sifRequest.requestId.orNull.equals(requestId))
    assert(sifRequest.serviceType.orNull.equals(SifServiceType.Functional))

    // request-specific
    assert(sifRequest.accept.orNull.equals(accept))
    assert(sifRequest.context.toString.equals(context.toString))
    assert(sifRequest.generatorId.orNull.equals(generatorId))
    assert(sifRequest.messageId.orNull.equals(messageId))
    assert(sifRequest.messageType.orNull.equals(messageType))
    assert(sifRequest.requestAction.orNull.equals(requestAction))
    assert(sifRequest.requestType.orNull.equals(requestType))
    assert(sifRequest.zone.toString.equals(zone.toString))
  }

  test("null provider") {
    val thrown = intercept[ArgumentNullException] {
      new SifRequest(null, "")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("provider parameter")))
  }

  test("null resourceUri") {
    val thrown = intercept[ArgumentNullException] {
      new SifRequest(SifTestValues.sifProvider, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("resourceUri parameter")))
  }

}
