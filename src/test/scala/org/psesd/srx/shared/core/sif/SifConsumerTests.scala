package org.psesd.srx.shared.core.sif

import org.scalatest.FunSuite

class SifConsumerTests extends FunSuite {

  test("query invalid uri") {
    val provider = new SifProvider(
      SifUri("https://psesd.hostedzone.com/invalid_uri"),
      SifProviderSessionToken("REDACTED"),
      SifProviderSharedSecret("REDACTED"),
      SifAuthenticationMethod.SifHmacSha256)
      val sifRequest = new SifRequest(provider, "invalid_resource")
      val response = new SifConsumer().query(sifRequest)
      assert(response.statusCode.equals(404))
  }

  ignore("query PRS Districts") {
    val provider = new SifProvider(
      SifUri("https://psesd.hostedzone.com/svcs/TEST/requestProvider/filters;zoneId=1;contextId=DEFAULT"),
      SifProviderSessionToken("REDACTED"),
      SifProviderSharedSecret("REDACTED"),
      SifAuthenticationMethod.SifHmacSha256)
    val requestId = "1234"
    val serviceType = SifServiceType.Object
    val accept = SifAccept.Xml
    val generatorId = "5678"
    val messageId = SifMessageId()
    val messageType = SifMessageType.Request
    val requestAction = SifRequestAction.Query
    val requestType = SifRequestType.Immediate

    val sifRequest = new SifRequest(provider, "districts")
    sifRequest.requestId = Option(requestId)
    sifRequest.serviceType = Option(serviceType)
    sifRequest.accept = Option(accept)
    sifRequest.generatorId = Option(generatorId)
    sifRequest.messageId = Option(messageId)
    sifRequest.messageType = Option(messageType)
    sifRequest.requestAction = Option(requestAction)
    sifRequest.requestType = Option(requestType)

    val response = new SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(200))
    assert(response.responseAction.orNull.equals(SifRequestAction.Query))
    assert(response.body.orNull.length > 0)
  }

}
