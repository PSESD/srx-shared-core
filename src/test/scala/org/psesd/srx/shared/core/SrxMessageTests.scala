package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

import scala.xml.Node

class SrxMessageTests extends FunSuite {

  val messageId = SifMessageId()
  val timestamp = SifTimestamp()
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
  val headers = "content-type: xml"
  val body = "body"

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
    Some(headers),
    Some(body)
  )

  val messageXml: Node = <message>
    <messageId>{messageId.toString}</messageId>
    <timestamp>{timestamp.toString}</timestamp>
    <component>{TestValues.srxService.service.name}</component>
    <componentVersion>{TestValues.srxService.service.version}</componentVersion>
    <resource>{resource}</resource>
    <method>{method}</method>
    <status>{status}</status>
    <generatorId>{generatorId}</generatorId>
    <requestId>{requestId}</requestId>
    <zoneId>{zone.toString}</zoneId>
    <contextId>{context.toString}</contextId>
    <studentId>{studentId}</studentId>
    <description>{description}</description>
    <uri>{uri}</uri>
    <userAgent>{userAgent}</userAgent>
    <sourceIp>{sourceIp}</sourceIp>
    <headers>{headers}</headers>
    <body>{body}</body>
  </message>

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
    assert(testMessage.messageId.toString.equals(messageId.toString))
    assert(testMessage.timestamp.toString.equals(timestamp.toString))
    assert(testMessage.srxService.service.name.equals(TestValues.srxService.service.name))
    assert(testMessage.srxService.service.version.equals(TestValues.srxService.service.version))
    assert(testMessage.resource.get.toString.equals(resource))
    assert(testMessage.method.get.toString.equals(method))
    assert(testMessage.status.get.toString.equals(status))
    assert(testMessage.generatorId.get.toString.equals(generatorId))
    assert(testMessage.requestId.get.toString.equals(requestId))
    assert(testMessage.zone.get.toString.equals(zone.toString))
    assert(testMessage.context.get.toString.equals(context.toString))
    assert(testMessage.studentId.get.toString.equals(studentId))
    assert(testMessage.description.equals(description))
    assert(testMessage.uri.get.toString.equals(uri))
    assert(testMessage.userAgent.get.toString.equals(userAgent))
    assert(testMessage.sourceIp.get.toString.equals(sourceIp))
    assert(testMessage.headers.get.toString.equals(headers))
    assert(testMessage.body.get.toString.equals(body))
  }

  test("request message") {
    val headersLinux = "x-forwarded-for: sourceIp\ntimestamp: 2015-02-24T20:51:59.878Z\nrequestType: IMMEDIATE\ngeneratorId: generatorId\nrequestId: requestId\nmessageType: REQUEST\nserviceType: OBJECT\nContent-Type: application/xml; charset=UTF-8\nuser-agent: userAgent\naccept: application/xml; charset=UTF-8\nrequestAction: QUERY"
    val headersWindows = "x-forwarded-for: sourceIp\r\ntimestamp: 2015-02-24T20:51:59.878Z\r\nrequestType: IMMEDIATE\r\ngeneratorId: generatorId\r\nrequestId: requestId\r\nmessageType: REQUEST\r\nserviceType: OBJECT\r\nContent-Type: application/xml; charset=UTF-8\r\nuser-agent: userAgent\r\naccept: application/xml; charset=UTF-8\r\nrequestAction: QUERY"
    val sifRequest = new SifRequest(SifTestValues.sifProvider, "xSre", SifZone(), SifContext(), SifTestValues.timestamp)
    sifRequest.requestAction = Some(SifRequestAction.Query)
    sifRequest.generatorId = Some(generatorId)
    sifRequest.requestId = Some(requestId)
    sifRequest.setUri(SifUri(uri))
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

  test("from xml") {
    val message = SrxMessage(messageXml)
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

  test("required xml") {
    val messageXml = <message>
      <messageId>{messageId.toString}</messageId>
      <timestamp>{timestamp.toString}</timestamp>
      <component>{TestValues.srxService.service.name}</component>
      <componentVersion>{TestValues.srxService.service.version}</componentVersion>
      <description>{description}</description>
    </message>
    val message = SrxMessage(messageXml)
    assert(message.messageId.toString.equals(messageId.toString))
    assert(message.timestamp.toString.equals(timestamp.toString))
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
    assert(message.description.equals(description))
    assert(message.uri.isEmpty)
    assert(message.userAgent.isEmpty)
    assert(message.sourceIp.isEmpty)
    assert(message.headers.isEmpty)
    assert(message.body.isEmpty)
  }

  test("xml missing messageId") {
    val messageXml = <message>
      <timestamp>{timestamp.toString}</timestamp>
      <component>{TestValues.srxService.service.name}</component>
      <componentVersion>{TestValues.srxService.service.version}</componentVersion>
      <description>{description}</description>
    </message>
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SrxMessage(messageXml)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("message.messageId")))
  }

  test("xml invalid messageId") {
    val messageXml = <message>
      <messageId>123</messageId>
      <timestamp>{timestamp.toString}</timestamp>
      <component>{TestValues.srxService.service.name}</component>
      <componentVersion>{TestValues.srxService.service.version}</componentVersion>
      <description>{description}</description>
    </message>
    val thrown = intercept[ArgumentInvalidException] {
      SrxMessage(messageXml)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("id parameter value '123'")))
  }

  test("xml missing timestamp") {
    val messageXml = <message>
      <messageId>{messageId.toString}</messageId>
      <component>{TestValues.srxService.service.name}</component>
      <componentVersion>{TestValues.srxService.service.version}</componentVersion>
      <description>{description}</description>
    </message>
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SrxMessage(messageXml)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("message.timestamp")))
  }

  test("xml invalid timestamp") {
    val messageXml = <message>
      <messageId>{messageId.toString}</messageId>
      <timestamp>123</timestamp>
      <component>{TestValues.srxService.service.name}</component>
      <componentVersion>{TestValues.srxService.service.version}</componentVersion>
      <description>{description}</description>
    </message>
    val thrown = intercept[ArgumentInvalidException] {
      SrxMessage(messageXml)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("dateTime parameter value '123'")))
  }

  test("xml missing component") {
    val messageXml = <message>
      <messageId>{messageId.toString}</messageId>
      <timestamp>{timestamp.toString}</timestamp>
      <componentVersion>{TestValues.srxService.service.version}</componentVersion>
      <description>{description}</description>
    </message>
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SrxMessage(messageXml)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("message.component")))
  }

  test("xml missing component version") {
    val messageXml = <message>
      <messageId>{messageId.toString}</messageId>
      <timestamp>{timestamp.toString}</timestamp>
      <component>{TestValues.srxService.service.name}</component>
      <description>{description}</description>
    </message>
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SrxMessage(messageXml)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("message.componentVersion")))
  }

  test("xml missing description") {
    val messageXml = <message>
      <messageId>{messageId.toString}</messageId>
      <timestamp>{timestamp.toString}</timestamp>
      <component>{TestValues.srxService.service.name}</component>
      <componentVersion>{TestValues.srxService.service.version}</componentVersion>
    </message>
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      SrxMessage(messageXml)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("message.description")))
  }

  test("invalid xml") {
    val messageXml = <foo></foo>
    val thrown = intercept[ArgumentInvalidException] {
      SrxMessage(messageXml)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("root element 'foo'")))
  }

  test("from XML string") {
    val messageXmlString = messageXml.toXmlString
    val message = SrxMessage.fromXmlString(messageXmlString)
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

  test("from JSON string") {
    val messageJsonString = messageXml.toJsonString
    val message = SrxMessage.fromJsonString(messageJsonString)
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

  test("toXml") {
    val xmlString = SrxMessage(messageXml).toXml.toXmlString
    assert(xmlString.contains(messageId.toString))
  }

  test("toJson") {
    val jsonString = SrxMessage(messageXml).toJson.toJsonString
    assert(jsonString.contains(messageId.toString))
  }

}
