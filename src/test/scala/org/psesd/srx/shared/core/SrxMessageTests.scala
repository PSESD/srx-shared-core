package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

class SrxMessageTests extends FunSuite {

  test("simple message") {
    val message = SrxMessage(TestValues.srxService, "test")
    assert(message.messageId.toString.length.equals(36))
    assert(message.timestamp.toString.length > 0)
    assert(message.srxService.service.name.equals(TestValues.srxService.service.name))
    assert(message.srxService.service.version.equals(TestValues.srxService.service.version))
    assert(message.resource.isEmpty)
    assert(message.method.isEmpty)
    assert(message.status.isEmpty)
    assert(message.generatorId.isEmpty)
    assert(message.requestId.isEmpty)
    assert(message.zone.isEmpty)
    assert(message.context.isEmpty)
    assert(message.studentId.isEmpty)
    assert(message.description.equals("test"))
    assert(message.uri.isEmpty)
    assert(message.userAgent.isEmpty)
    assert(message.sourceIp.isEmpty)
    assert(message.headers.isEmpty)
    assert(message.body.isEmpty)
    assert(message.srxRequest.isEmpty)
  }

  test("non-request message") {
    val messageId = SifMessageId()
    val timestamp = SifTimestamp()
    val resource = "xSre"
    val method = "query"
    val status = "status"
    val generatorId = "generatorId"
    val requestId = "requestId"
    val zone = SifZone("testZone")
    val context = SifContext("testContext")
    val studentId = "studentId"
    val description = "description"
    val uri = "http://localhost/test"
    val userAgent = "userAgent"
    val sourceIp = "sourceIp"
    val headers = "content-type: xml"
    val body = "body"
    val message = SrxMessage(
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
      Some(headers),
      Some(body)
    )
    assert(message.messageId.toString.equals(messageId.toString))
    assert(message.timestamp.toString.equals(timestamp.toString))
    assert(message.srxService.service.name.equals(TestValues.srxService.service.name))
    assert(message.srxService.service.version.equals(TestValues.srxService.service.version))
    assert(message.resource.get.toString.equals(resource))
    assert(message.method.get.toString.equals(method))
    assert(message.status.get.toString.equals(status))
    assert(message.generatorId.get.toString.equals(generatorId))
    assert(message.requestId.get.toString.equals(requestId))
    assert(message.zone.get.toString.equals(zone.toString))
    assert(message.context.get.toString.equals(context.toString))
    assert(message.studentId.get.toString.equals(studentId))
    assert(message.description.equals(description))
    assert(message.uri.get.toString.equals(uri))
    assert(message.userAgent.get.toString.equals(userAgent))
    assert(message.sourceIp.get.toString.equals(sourceIp))
    assert(message.headers.get.toString.equals(headers))
    assert(message.body.get.toString.equals(body))
  }

  test("request message") {
    val resource = "xSre"
    val method = "QUERY"
    val status = "status"
    val generatorId = "generatorId"
    val requestId = "requestId"
    val zone = SifZone()
    val context = SifContext()
    val studentId = "studentId"
    val description = "description"
    val uri = "https://localhost/xSre;zoneId=DEFAULT;contextId=DEFAULT"
    val userAgent = "userAgent"
    val sourceIp = "sourceIp"
    val headersLinux = "x-forwarded-for: sourceIp\ntimestamp: 2015-02-24T20:51:59.878Z\nrequestType: IMMEDIATE\ngeneratorId: generatorId\nrequestId: requestId\nmessageType: REQUEST\nserviceType: OBJECT\nContent-Type: application/xml; charset=UTF-8\nuser-agent: userAgent\naccept: application/xml; charset=UTF-8\nauthorization: SIF_HMACSHA256 YWQ1M2RiZjYtZTBhMC00NjlmLTg0MjgtYzE3NzM4ZWJhNDNlOmpVSnprUWhBWDBaSHB3a0VPSmMzQnE2dENjSjB2VUd3RGRMRndVdHFPSjA9\nrequestAction: QUERY"
    val headersWindows = "x-forwarded-for: sourceIp\r\ntimestamp: 2015-02-24T20:51:59.878Z\r\nrequestType: IMMEDIATE\r\ngeneratorId: generatorId\r\nrequestId: requestId\r\nmessageType: REQUEST\r\nserviceType: OBJECT\r\nContent-Type: application/xml; charset=UTF-8\r\nuser-agent: userAgent\r\naccept: application/xml; charset=UTF-8\r\nauthorization: SIF_HMACSHA256 YWQ1M2RiZjYtZTBhMC00NjlmLTg0MjgtYzE3NzM4ZWJhNDNlOmpVSnprUWhBWDBaSHB3a0VPSmMzQnE2dENjSjB2VUd3RGRMRndVdHFPSjA9\r\nrequestAction: QUERY"
    val body = "body"
    val sifRequest = new SifRequest(SifTestValues.sifProvider, "xSre", SifZone(), SifContext(), SifTestValues.timestamp)
    sifRequest.requestAction = Some(SifRequestAction.Query)
    sifRequest.generatorId = Some(generatorId)
    sifRequest.requestId = Some(requestId)
    sifRequest.uri = SifUri(uri)
    sifRequest.addHeader(SifHttpHeader.UserAgent.toString, userAgent)
    sifRequest.addHeader(SifHttpHeader.ForwardedFor.toString, sourceIp)
    sifRequest.body = Some(body)
    val srxRequest = SrxRequest(sifRequest)
    val message = SrxMessage(
      TestValues.srxService,
      Some(resource),
      Some(status),
      Some(studentId),
      description,
      Some(srxRequest)
    )
    assert(message.messageId.toString.length.equals(36))
    assert(message.timestamp.toString.length > 0)
    assert(message.srxService.service.name.equals(TestValues.srxService.service.name))
    assert(message.srxService.service.version.equals(TestValues.srxService.service.version))
    assert(message.resource.get.toString.equals(resource))
    assert(message.method.get.toString.equals(method))
    assert(message.status.get.toString.equals(status))
    assert(message.generatorId.get.toString.equals(generatorId))
    assert(message.requestId.get.toString.equals(requestId))
    assert(message.zone.get.toString.equals(zone.toString))
    assert(message.context.get.toString.equals(context.toString))
    assert(message.studentId.get.toString.equals(studentId))
    assert(message.description.equals(description))
    assert(message.getUri.equals(uri))
    assert(message.getUserAgent.equals(userAgent))
    assert(message.getSourceIp.toString.equals(sourceIp))
    val actualHeaders = message.getHeaders
    assert(actualHeaders.equals(headersLinux) || actualHeaders.equals(headersWindows))
    assert(message.getBody.toString.equals(body))
  }

}
