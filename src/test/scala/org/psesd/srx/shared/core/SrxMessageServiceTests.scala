package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

class SrxMessageServiceTests extends FunSuite {

  val messageId = SifMessageId()
  val timestamp = SifTimestamp()
  val resource = SrxResourceType.Xsres.toString
  val method = SifRequestAction.Query.toString
  val status = SrxMessageStatus.Success.toString
  val generatorId = "AutomatedTests"
  val requestId = "testRequestId"
  val zone = SifZone()
  val context = SifContext()
  val studentId = "testStudentId"
  val description = "test description"
  val uri = "https://localhost/%s;zoneId=DEFAULT;contextId=DEFAULT".format(SrxResourceType.Xsres.toString)
  val userAgent = "testUserAgent"
  val sourceIp = "testSourceIp"
  val body = "test body"
  val service = TestValues.srxService
  val requestParameters = SifRequestParameterCollection (List(SifRequestParameter("generatorId", generatorId), SifRequestParameter("requestId", requestId),SifRequestParameter("contextId", context.toString), SifRequestParameter("uri", uri), SifRequestParameter("userAgent", userAgent), SifRequestParameter("sourceIp", sourceIp)))

  val testMessage = SrxMessage(
    TestValues.srxService,
    messageId,
    timestamp,
    Some(resource),
    Some(method),
    Some(status),
    Some(generatorId),
    Some(requestId),
    Some(zone),
    Some(context),
    Some(studentId),
    description,
    Some(uri),
    Some(userAgent),
    Some(sourceIp),
    None,
    Some(body)
  )

  ignore("message create") {
    val response = SrxMessageService.createMessage("srx-shared-core", testMessage)
    assert(response.statusCode.equals(SifHttpStatusCode.Created))
    val body = response.body.get
    assert(body.contains("\"advisoryId\" : \"1\""))
    assert(body.contains("\"statusCode\" : \"201\""))
  }

  test("queryMessage") {
    val messageResource = SrxResourceType.SrxMessages.toString + "/0bfea538-f1b7-4f18-8115-b719da189f04"
    val response = SrxMessageService.queryMessage(messageResource, zone, context)
    assert(response.statusCode.equals(SifHttpStatusCode.Ok))
  }

  test ("request message create") {
    val response = SrxMessageService.createRequestMessage(method,zone.toString, studentId, requestParameters, service, Some(resource), None)
    assert(response.statusCode.equals(SifHttpStatusCode.Created))
    val body = response.getBodyXml.get.toString
    val bodyXml = xml.XML.loadString(body)
    val messageId = (bodyXml \\ "id").text
    val newMessageResource = SrxResourceType.SrxMessages.toString + "/" + messageId
    val message = SrxMessageService.queryMessage(newMessageResource, zone, context)
    assert(message.statusCode.equals(SifHttpStatusCode.Ok))

    val queriedMessage = xml.XML.loadString(message.getBodyXml.get.toString)
    assert((queriedMessage \\"generatorId").text.equals("AutomatedTests"))
    assert((queriedMessage \\"studentId").text.equals("testStudentId"))
    assert((queriedMessage \\"headers").text != null)
  }

}
