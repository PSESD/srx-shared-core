package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.CoreResource
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ExceptionMessage}
import org.scalatest.FunSuite

class SifConsumerTests extends FunSuite {

  private lazy val srxSessionToken = SifProviderSessionToken(Environment.getProperty(Environment.SrxSessionTokenKey))
  private lazy val srxSharedSecret =SifProviderSharedSecret(Environment.getProperty(Environment.SrxSharedSecretKey))

  ignore("query invalid uri") {
    // ignoring in build environment due to expected long runtime (30 second connection timeout)
    val provider = new SifProvider(
      SifProviderUrl("https://hostedzone.com/invalid_uri"),
      SifProviderSessionToken("INVALID"),
      SifProviderSharedSecret("INVALID"),
      SifAuthenticationMethod.SifHmacSha256)
    val sifRequest = new SifRequest(provider, "invalid_resource")
    val response = SifConsumer().query(sifRequest)
    assert(response.exceptions(0).getMessage.startsWith("Connect to hostedzone.com:443") && response.exceptions(0).getMessage.contains("failed"))
  }

  test("query invalid subdomain uri") {
    val provider = new SifProvider(
      SifProviderUrl("https://psesd.hostedzone.com/invalid_uri"),
      SifProviderSessionToken("INVALID"),
      SifProviderSharedSecret("INVALID"),
      SifAuthenticationMethod.SifHmacSha256)
    val sifRequest = new SifRequest(provider, "invalid_resource")
    val response = SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(SifHttpStatusCode.NotFound))
    assert(response.body.get.contains("The requested resource is not available"))
    assert(response.exceptions(0).getMessage.equals("Response contains invalid Content-Type: 'text/html;charset=utf-8'."))
  }

  test("query not https") {
    val provider = new SifProvider(
      SifProviderUrl("http://psesd.hostedzone.com/svcs/dev/requestProvider"),
      srxSessionToken,
      srxSharedSecret,
      SifAuthenticationMethod.SifHmacSha256)
    val sifRequest = new SifRequest(provider, "filters", SifZone("test"))
    val response = SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(SifHttpStatusCode.BadRequest))
    assert(response.body.get.contains("Call MUST be SSL"))
  }

  test("query invalid session token") {
    val provider = new SifProvider(
      Environment.srxEnvironmentUrl,
      SifProviderSessionToken("test"),
      srxSharedSecret,
      SifAuthenticationMethod.SifHmacSha256)
    val sifRequest = new SifRequest(provider, "filters", SifZone("test"))
    val response = SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(SifHttpStatusCode.Unauthorized))
    assert(response.body.get.contains("Environment with sessionId[test] does not exist"))
  }

  test("query invalid shared secret") {
    val provider = new SifProvider(
      Environment.srxEnvironmentUrl,
      srxSessionToken,
      SifProviderSharedSecret("test"),
      SifAuthenticationMethod.SifHmacSha256)
    val sifRequest = new SifRequest(provider, "filters", SifZone("test"))
    val response = SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(SifHttpStatusCode.Unauthorized))
    assert(response.body.get.contains("Bad credential"))
  }

  test("query invalid resource") {
    val sifRequest = new SifRequest(Environment.srxProvider, "invalid_resource")
    val response = SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(SifHttpStatusCode.NotFound))
    assert(response.body.get.contains("Service[invalid_resource] not found"))
  }

  test("query PRS filters DIRECT to PRS") {
    if(Environment.isLocal) {
      // local environment only - PRS environment variables not configured in build environment
      // also, this test should ultimately fail when PRS whitelist rejects local IPs
      val provider = new SifProvider(
        SifProviderUrl(Environment.getProperty("SRX_PRS_DIRECT_URL")),
        SifProviderSessionToken(Environment.getProperty("SRX_PRS_SESSION_TOKEN")),
        SifProviderSharedSecret(Environment.getProperty("SRX_PRS_SHARED_SECRET")),
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

      val response = SifConsumer().query(sifRequest)
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
      assert(response.responseAction.orNull.equals(SifRequestAction.Query))
      assert(response.body.orNull.length > 0)
    }
  }

  test("query PRS filters") {
    val requestId = "1234"
    val serviceType = SifServiceType.Object
    val accept = SifContentType.Xml
    val generatorId = "5678"
    val messageId = SifMessageId()
    val messageType = SifMessageType.Request
    val requestAction = SifRequestAction.Query
    val requestType = SifRequestType.Immediate

    val sifRequest = new SifRequest(Environment.srxProvider, "filters", SifZone("test"))
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

    val response = SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(SifHttpStatusCode.Ok))
    assert(response.responseAction.orNull.equals(SifRequestAction.Query))
    assert(response.body.orNull.length > 0)
  }

  test("query valid xSRE") {
    val requestId = "1234"
    val serviceType = SifServiceType.Object
    val accept = SifContentType.Xml
    val generatorId = "5678"
    val messageId = SifMessageId()
    val messageType = SifMessageType.Request
    val requestAction = SifRequestAction.Query
    val requestType = SifRequestType.Immediate

    val sifRequest = new SifRequest(Environment.srxProvider, "xSres/sample1", SifZone("seattle"))
    sifRequest.requestId = Option(requestId)
    sifRequest.serviceType = Option(serviceType)
    sifRequest.accept = Option(accept)
    sifRequest.generatorId = Option(generatorId)
    sifRequest.messageId = Option(messageId)
    sifRequest.messageType = Option(messageType)
    sifRequest.requestAction = Option(requestAction)
    sifRequest.requestType = Option(requestType)

    val response = SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(SifHttpStatusCode.Ok))
    assert(response.responseAction.orNull.equals(SifRequestAction.Query))
    assert(response.body.get.contains("sample1"))
  }

  test("query invalid xSRE") {
    val requestId = "1234"
    val serviceType = SifServiceType.Object
    val accept = SifContentType.Xml
    val generatorId = "5678"
    val messageId = SifMessageId()
    val messageType = SifMessageType.Request
    val requestAction = SifRequestAction.Query
    val requestType = SifRequestType.Immediate

    val sifRequest = new SifRequest(Environment.srxProvider, "xSres/notfound", SifZone("seattle"))
    sifRequest.requestId = Option(requestId)
    sifRequest.serviceType = Option(serviceType)
    sifRequest.accept = Option(accept)
    sifRequest.generatorId = Option(generatorId)
    sifRequest.messageId = Option(messageId)
    sifRequest.messageType = Option(messageType)
    sifRequest.requestAction = Option(requestAction)
    sifRequest.requestType = Option(requestType)

    val response = SifConsumer().query(sifRequest)
    assert(response.statusCode.equals(SifHttpStatusCode.NotFound))
    assert(response.body.get.contains("Not Found"))
  }

  test("create empty body") {
    val sifRequest = new SifRequest(Environment.srxProvider, CoreResource.SrxMessages.toString)
    val thrown = intercept[ArgumentInvalidException] {
      SifConsumer().create(sifRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("request body")))
  }

  test("update empty body") {
    val sifRequest = new SifRequest(Environment.srxProvider, CoreResource.SrxMessages.toString)
    val thrown = intercept[ArgumentInvalidException] {
      SifConsumer().update(sifRequest)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("request body")))
  }

}
