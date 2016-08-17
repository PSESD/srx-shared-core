package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

class SrxMessageServiceTests extends FunSuite {

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

}
