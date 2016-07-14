package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.SifContentTypeInvalidException
import org.scalatest.FunSuite

class SifConsumerTests extends FunSuite {

  val environmentProviderUrl = Environment.getProperty(Environment.EnvironmentProviderUrlKey)
  val environmentProviderSessionToken = Environment.getProperty(Environment.EnvironmentProviderSessionTokenKey)
  val environmentProviderSharedSecret = Environment.getProperty(Environment.EnvironmentProviderSharedSecretKey)

  test("query invalid uri") {
    val provider = new SifProvider(
      SifProviderUrl("https://psesd.hostedzone.com/invalid_uri"),
      SifProviderSessionToken("INVALID"),
      SifProviderSharedSecret("INVALID"),
      SifAuthenticationMethod.SifHmacSha256)
    val sifRequest = new SifRequest(provider, "invalid_resource")
    val thrown = intercept[SifContentTypeInvalidException] {
      new SifConsumer().query(sifRequest)
    }
    assert(thrown.getMessage.equals("Response contains invalid Content-Type: 'text/html;charset=utf-8'."))
    // assert(response.statusCode.equals(404))
  }

  ignore("query PRS filters DIRECT to PRS") {
    val provider = new SifProvider(
      SifProviderUrl(Environment.getProperty("PRS_DIRECT_URL")),
      SifProviderSessionToken(Environment.getProperty("PRS_DIRECT_SESSION_TOKEN")),
      SifProviderSharedSecret(Environment.getProperty("PRS_DIRECT_SHARED_SECRET")),
      SifAuthenticationMethod.SifHmacSha256)
    val requestId = "1234"
    val serviceType = SifServiceType.Object
    val accept = SifContentType.Xml
    val generatorId = "5678"
    val messageId = SifMessageId()
    val messageType = SifMessageType.Request
    val requestAction = SifRequestAction.Query
    val requestType = SifRequestType.Immediate

    val sifRequest = new SifRequest(provider, "filters")
    sifRequest.requestId = Option(requestId)
    sifRequest.serviceType = Option(serviceType)
    sifRequest.accept = Option(accept)
    sifRequest.generatorId = Option(generatorId)
    sifRequest.messageId = Option(messageId)
    sifRequest.messageType = Option(messageType)
    sifRequest.requestAction = Option(requestAction)
    sifRequest.requestType = Option(requestType)

    // add custom headers specific to the PRS filters endpoint
    sifRequest.addHeader("objectType", "xSre")
    sifRequest.addHeader("externalServiceId", "1")
    sifRequest.addHeader("districtStudentId", "1")
    sifRequest.addHeader("authorizedEntityId", "1")

    val response = new SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(200))
    assert(response.responseAction.orNull.equals(SifRequestAction.Query))
    assert(response.body.orNull.length > 0)
  }

  test("query PRS filters") {
    val provider = new SifProvider(
      SifProviderUrl(environmentProviderUrl),
      SifProviderSessionToken(environmentProviderSessionToken),
      SifProviderSharedSecret(environmentProviderSharedSecret),
      SifAuthenticationMethod.SifHmacSha256)
    val requestId = "1234"
    val serviceType = SifServiceType.Object
    val accept = SifContentType.Xml
    val generatorId = "5678"
    val messageId = SifMessageId()
    val messageType = SifMessageType.Request
    val requestAction = SifRequestAction.Query
    val requestType = SifRequestType.Immediate

    val sifRequest = new SifRequest(provider, "filters", SifZone("test"))
    sifRequest.requestId = Option(requestId)
    sifRequest.serviceType = Option(serviceType)
    sifRequest.accept = Option(accept)
    sifRequest.generatorId = Option(generatorId)
    sifRequest.messageId = Option(messageId)
    sifRequest.messageType = Option(messageType)
    sifRequest.requestAction = Option(requestAction)
    sifRequest.requestType = Option(requestType)

    // add custom headers specific to the PRS filters endpoint
    sifRequest.addHeader("objectType", "xSre")
    sifRequest.addHeader("externalServiceId", "1")
    sifRequest.addHeader("districtStudentId", "1")
    sifRequest.addHeader("authorizedEntityId", "1")

    val response = new SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(200))
    assert(response.responseAction.orNull.equals(SifRequestAction.Query))
    assert(response.body.orNull.length > 0)
  }

  test("query xSRE") {
    val provider = new SifProvider(
      SifProviderUrl(environmentProviderUrl),
      SifProviderSessionToken(environmentProviderSessionToken),
      SifProviderSharedSecret(environmentProviderSharedSecret),
      SifAuthenticationMethod.SifHmacSha256)
    val requestId = "1234"
    val serviceType = SifServiceType.Object
    val accept = SifContentType.Xml
    val generatorId = "5678"
    val messageId = SifMessageId()
    val messageType = SifMessageType.Request
    val requestAction = SifRequestAction.Query
    val requestType = SifRequestType.Immediate

    val sifRequest = new SifRequest(provider, "xSres/sample1", SifZone("seattle"))
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
