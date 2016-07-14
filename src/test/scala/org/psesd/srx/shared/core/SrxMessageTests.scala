package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.sif.{SifMessageId, SifTimestamp}
import org.scalatest.FunSuite

class SrxMessageTests extends FunSuite {

  test("empty message") {
    val message = SrxMessage.getEmpty
    assert(message.messageId.getOrElse(SifMessageId()).toString.length.equals(36))
    assert(message.timestamp.toString.length > 0)
    assert(message.operation.getOrElse("").equals(""))
    assert(message.status.getOrElse("").equals(""))
    assert(message.source.getOrElse("").equals(""))
    assert(message.destination.getOrElse("").equals(""))
    assert(message.description.getOrElse("").equals(""))
    assert(message.body.getOrElse("").equals(""))
    assert(message.sourceIp.getOrElse("").equals(""))
    assert(message.userAgent.getOrElse("").equals(""))
    assert(message.requestContext.orNull == null)
  }

  test("valid message") {
    val messageId = SifMessageId()
    val timestamp = SifTimestamp()
    val operation = SrxOperation.None
    val status = SrxOperationStatus.None
    val source = "source"
    val destination = "destination"
    val description = "description"
    val body = "body"
    val sourceIp = "sourceIp"
    val userAgent = "userAgent"
    val srxRequest = new SrxRequest(0, null, null, null)
    val srxRequestMessageId = srxRequest.messageId
    val message = SrxMessage(
      Option(messageId),
      timestamp,
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
    assert(message.messageId.orNull.toString.equals(messageId.toString))
    assert(message.timestamp.toString.equals(timestamp.toString))
    assert(message.operation.orNull.toString.equals(operation.toString))
    assert(message.status.orNull.toString.equals(status.toString))
    assert(message.source.getOrElse("").equals(source))
    assert(message.destination.getOrElse("").equals(destination))
    assert(message.description.getOrElse("").equals(description))
    assert(message.body.getOrElse("").equals(body))
    assert(message.sourceIp.getOrElse("").equals(sourceIp))
    assert(message.userAgent.getOrElse("").equals(userAgent))
    assert(message.requestContext.orNull.messageId.toString.equals(srxRequestMessageId.toString))
  }

}
