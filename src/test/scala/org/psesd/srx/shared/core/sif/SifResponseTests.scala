package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.scalatest.FunSuite

class SifResponseTests extends FunSuite {

  val timestamp = SifTimestamp()
  val messageId = SifMessageId("ad53dbf6-e0a0-469f-8428-c17738eba43e")
  val messageType = SifMessageType.Response
  val sifRequest = new SifRequest(SifTestValues.sifProvider, "", SifZone(), SifContext(), timestamp)

  test("default response") {
    val sifResponse = new SifResponse(timestamp, messageId, messageType, sifRequest)

    // constructor
    assert(sifResponse.timestamp.toString.equals(timestamp.toString))
    assert(sifResponse.messageId.equals(messageId))
    assert(sifResponse.messageType.equals(messageType))
    assert(sifResponse.sifRequest.equals(sifRequest))

    // base class
    assert(sifResponse.requestId.isEmpty)
    assert(sifResponse.serviceType.orNull.equals(SifServiceType.Object))

    // response-specific
    assert(sifResponse.responseAction.isEmpty)
  }


  test("fully constructed response") {
    val requestId = "1234"
    val serviceType = SifServiceType.Functional
    sifRequest.requestAction = Option(SifRequestAction.Update)
    val sifResponse = new SifResponse(timestamp, messageId, messageType, sifRequest)
    sifResponse.requestId = Option(requestId)
    sifResponse.serviceType = Option(serviceType)

    // constructor
    assert(sifResponse.timestamp.toString.equals(timestamp.toString))
    assert(sifResponse.messageId.equals(messageId))
    assert(sifResponse.messageType.equals(messageType))
    assert(sifResponse.sifRequest.equals(sifRequest))

    // base class
    assert(sifResponse.requestId.orNull.equals(requestId))
    assert(sifResponse.serviceType.orNull.equals(SifServiceType.Functional))

    // response-specific
    assert(sifResponse.responseAction.orNull.equals(SifRequestAction.Update))
  }

  test("null timestamp") {
    val thrown = intercept[ArgumentNullException] {
      new SifResponse(null, messageId, messageType, sifRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("timestamp parameter")))
  }

  test("null messageId") {
    val thrown = intercept[ArgumentNullException] {
      new SifResponse(timestamp, null, messageType, sifRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("messageId parameter")))
  }

  test("null messageType") {
    val thrown = intercept[ArgumentNullException] {
      new SifResponse(timestamp, messageId, null, sifRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("messageType parameter")))
  }

  test("getBodyJson from empty string") {
    val sifResponse = new SifResponse(timestamp, messageId, messageType, sifRequest)
    sifResponse.body = Some("")
    assert(sifResponse.getBodyJson.isEmpty)
  }

  test("getBodyJson from json string") {
    val sifResponse = new SifResponse(timestamp, messageId, messageType, sifRequest)
    sifResponse.body = Some("{\"test1\": {\"test2\": {}} }")
    assert(sifResponse.getBodyJson.get.children.length == 1)
  }

  test("getBodyJson from xml string") {
    val sifResponse = new SifResponse(timestamp, messageId, messageType, sifRequest)
    sifResponse.body = Some(<test1><test2/></test1>.toString)
    assert(sifResponse.getBodyJson.get.children.length == 1)
  }

  test("getBodyXml from json string") {
    val sifResponse = new SifResponse(timestamp, messageId, messageType, sifRequest)
    sifResponse.body = Some("{\"test1\": {\"test2\": {}} }")
    assert((sifResponse.getBodyXml.get \ "test1") != null)
  }

  test("getBodyXml from xml string") {
    val sifResponse = new SifResponse(timestamp, messageId, messageType, sifRequest)
    sifResponse.body = Some(<test1><test2/></test1>.toString)
    assert((sifResponse.getBodyXml.get \ "test1") != null)
  }

}
