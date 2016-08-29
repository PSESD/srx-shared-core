package org.psesd.srx.shared.core

import java.util.UUID

import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.http4s.dsl._
import org.http4s.{HttpService, Method, Request}
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.extensions.HttpTypeExtensions._
import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class SrxServerTests extends FunSuite {

  private final val ServerDuration = 5000

  private val pendingInterrupts = new ThreadLocal[List[Thread]] {
    override def initialValue = Nil
  }

  private lazy val tempServer = Future {
    delayedInterrupt(ServerDuration)
    intercept[InterruptedException] {
      startServer()
    }
  }

  test("root") {
    if(Environment.isLocal) {
      val getRoot = Request(Method.GET, uri("/"))
      val task = srxServer.service.run(getRoot)
      val response = task.run
      assert(response.status.code.equals(SifHttpStatusCode.Ok))
    }
  }

  test("ping") {
    if(Environment.isLocal) {
      val getPing = Request(Method.GET, uri("/ping"))
      val task = srxServer.service.run(getPing)
      val response = task.run
      val body = response.body.value
      assert(response.status.code.equals(SifHttpStatusCode.Ok))
      assert(body.equals(true.toString))
    }
  }

  test("ping (localhost)") {
    if(Environment.isLocal) {
      val expected = "true"
      var actual = ""
      tempServer onComplete {
        case Success(x) =>
          assert(actual.equals(expected))
        case _ =>
      }

      // wait for server to init
      Thread.sleep(2000)

      // ping server and collect response
      val httpclient: CloseableHttpClient = HttpClients.custom().disableCookieManagement().build()
      val httpGet = new HttpGet("http://localhost:%s/ping".format(Environment.getPropertyOrElse("SERVER_PORT", "80")))
      val response = httpclient.execute(httpGet)
      actual = EntityUtils.toString(response.getEntity)
    }
  }

  test("info (localhost)") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, "info")
      val response = new SifConsumer().query(sifRequest)
      val responseBody = response.body.getOrElse("")
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
      assert(response.contentType.get.equals(SifContentType.Xml))
      assert(responseBody.contains("<service>"))
    }
  }

  test("info json (localhost)") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, "info")
      sifRequest.accept = Option(SifContentType.Json)
      val response = new SifConsumer().query(sifRequest)
      val responseBody = response.body.getOrElse("")
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
      assert(response.contentType.get.equals(SifContentType.Json))
      assert(responseBody.contains("\"service\" : {"))
    }
  }

  test("info with invalid session token (localhost)") {
    if(Environment.isLocal) {
      val invalidProvider = new SifProvider(TestValues.sifUrl, SifProviderSessionToken(UUID.randomUUID.toString), TestValues.sharedSecret, TestValues.sifAuthenticationMethod)
      val sifRequest = new SifRequest(invalidProvider, "info")
      val response = new SifConsumer().query(sifRequest)
      val responseBody = response.body.getOrElse("")
      assert(response.statusCode.equals(SifHttpStatusCode.Unauthorized))
      assert(responseBody.contains("<scope>Info</scope>"))
      assert(responseBody.contains("<message>Unauthorized</message>"))
      assert(responseBody.contains("<description>SIF user or session '"))
    }
  }

  test("info with invalid shared secret (localhost)") {
    if(Environment.isLocal) {
      val invalidProvider = new SifProvider(TestValues.sifUrl, TestValues.sessionToken, SifProviderSharedSecret("invalid"), TestValues.sifAuthenticationMethod)
      val sifRequest = new SifRequest(invalidProvider, "info")
      val response = new SifConsumer().query(sifRequest)
      val responseBody = response.body.getOrElse("")
      assert(response.statusCode.equals(SifHttpStatusCode.Unauthorized))
      assert(responseBody.contains("<scope>Info</scope>"))
      assert(responseBody.contains("<message>Unauthorized</message>"))
      assert(responseBody.contains("<description>The authorization header is invalid.</description>"))
    }
  }

  test("info with invalid authentication method (localhost)") {
    if(Environment.isLocal) {
      val invalidProvider = new SifProvider(TestValues.sifUrl, TestValues.sessionToken, TestValues.sharedSecret, SifAuthenticationMethod.Basic)
      val sifRequest = new SifRequest(invalidProvider, "info")
      val response = new SifConsumer().query(sifRequest)
      val responseBody = response.body.getOrElse("")
      assert(response.statusCode.equals(SifHttpStatusCode.Unauthorized))
      assert(responseBody.contains("<scope>Info</scope>"))
      assert(responseBody.contains("<message>Unauthorized</message>"))
      assert(responseBody.contains("<description>SIF authentication method 'Basic' is invalid.</description>"))
    }
  }

  test("json info with invalid authentication method (localhost)") {
    if(Environment.isLocal) {
      val invalidProvider = new SifProvider(TestValues.sifUrl, TestValues.sessionToken, TestValues.sharedSecret, SifAuthenticationMethod.Basic)
      val sifRequest = new SifRequest(invalidProvider, "info")
      sifRequest.accept = Option(SifContentType.Json)
      val response = new SifConsumer().query(sifRequest)
      val responseBody = response.body.getOrElse("")
      assert(response.statusCode.equals(SifHttpStatusCode.Unauthorized))
      assert(response.contentType.get.equals(SifContentType.Json))
      assert(responseBody.contains("\"scope\" : \"Info\""))
      assert(responseBody.contains("\"message\" : \"Unauthorized\""))
      assert(responseBody.contains("\"description\" : \"SIF authentication method 'Basic' is invalid.\""))
    }
  }

  test("info (no headers)") {
    if(Environment.isLocal) {
      val getInfo = Request(Method.GET, uri("/info"))
      val task = srxServer.service.run(getInfo)
      val response = task.run
      val body = response.body.value
      assert(response.status.code.equals(SifHttpStatusCode.BadRequest))
      assert(body.contains("The sifUri is invalid."))
    }
  }

  test("create valid") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, TestValues.testEntitiesResource)
      sifRequest.parameters += SifRequestParameter("a", "b")
      sifRequest.parameters += SifRequestParameter("x", "y")
      sifRequest.body = Some("<test/>")
      val response = new SifConsumer().create(sifRequest)
      printlnResponse(response)
      assert(response.statusCode.equals(SifHttpStatusCode.Created))
      assert(response.getBody(SifContentType.Xml).equals("<test id=\"123\"/>"))
    }
  }

  test("create invalid xml") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, TestValues.testEntitiesResource)
      sifRequest.body = Some("invalid")
      val response = new SifConsumer().create(sifRequest)
      printlnResponse(response)
      assert(response.statusCode.equals(SifHttpStatusCode.BadRequest))
      assert(response.getBody(SifContentType.Xml).contains("<message>Failed to create testEntities.</message>"))
      assert(response.getBody(SifContentType.Xml).contains("<description>The request body XML is invalid.</description>"))
    }
  }

  test("create invalid xml content") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, TestValues.testEntitiesResource)
      sifRequest.body = Some("<invalid/>")
      val response = new SifConsumer().create(sifRequest)
      printlnResponse(response)
      assert(response.statusCode.equals(SifHttpStatusCode.BadRequest))
      assert(response.getBody(SifContentType.Xml).contains("<message>Failed to create testEntities.</message>"))
      assert(response.getBody(SifContentType.Xml).contains("<description>The root element 'invalid' is invalid.</description>"))
    }
  }

  test("query by id") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, TestValues.testEntitiesResource + "/1")
      val response = new SifConsumer().query(sifRequest)
      printlnResponse(response)
      assert(response.statusCode.equals(SifHttpStatusCode.Ok))
      assert(response.getBody(SifContentType.Xml).equals("<test id=\"1\"/>"))
    }
  }

  test("update not allowed") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, TestValues.testEntitiesResource)
      sifRequest.body = Some("<test/>")
      val response = new SifConsumer().update(sifRequest)
      printlnResponse(response)
      assert(response.statusCode.equals(SifHttpStatusCode.MethodNotAllowed))
    }
  }

  test("delete not allowed") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, TestValues.testEntitiesResource + "/1")
      val response = new SifConsumer().delete(sifRequest)
      printlnResponse(response)
      assert(response.statusCode.equals(SifHttpStatusCode.MethodNotAllowed))
    }
  }

  test("delete all not allowed") {
    if(Environment.isLocal) {
      val sifRequest = new SifRequest(TestValues.sifProvider, TestValues.testEntitiesResource)
      val response = new SifConsumer().delete(sifRequest)
      printlnResponse(response)
      assert(response.statusCode.equals(SifHttpStatusCode.MethodNotAllowed))
    }
  }

  private def delayedInterrupt(delay: Long) {
    delayedInterrupt(Thread.currentThread, delay)
  }

  private def delayedInterrupt(target: Thread, delay: Long) {
    val t = new Thread {
      override def run() {
        Thread.sleep(delay)
        target.interrupt()
      }
    }
    pendingInterrupts.set(t :: pendingInterrupts.get)
    t.start()
  }

  private def startServer(): Unit = {
    if(Environment.isLocal) {
      srxServer.main(Array[String]())
    }
  }

  private def printlnResponse(response: SifResponse): Unit = {
    println(response.sifRequest.getUri.toString)
    println("RETURNED: " + response.statusCode.toString)
    for (header <- response.getHeaders) {
      println("%s=%s".format(header._1, header._2))
    }
    println(response.getBody(SifContentType.Xml))
  }

  private object srxServer extends SrxServer {
    def srxService = TestValues.srxService

    def sifProvider = TestValues.sifProvider

    override def serviceRouter(implicit executionContext: ExecutionContext) = HttpService {
      case req@GET -> Root =>
        Ok()

      case _ -> Root =>
        NotImplemented()

      case req@GET -> Root / _ if services(req, CoreResource.Ping.toString) =>
        Ok(true.toString)

      case req@GET -> Root / _ if services(req, CoreResource.Info.toString) =>
        respondWithInfo(getDefaultSrxResponse(req))

      case req@DELETE -> Root / _ if services(req, TestValues.testEntitiesResource) =>
        executeRequest(req, None, TestValues.testEntitiesResource, TestValues.TestEntityService)

      case req@DELETE -> Root / TestValues.testEntitiesResource / _ =>
        executeRequest(req, None, TestValues.testEntitiesResource, TestValues.TestEntityService)

      case req@GET -> Root / TestValues.testEntitiesResource / _ =>
        executeRequest(req, None, TestValues.testEntitiesResource, TestValues.TestEntityService)

      case req@POST -> Root / _ if services(req, TestValues.testEntitiesResource) =>
        executeRequest(req, None, TestValues.testEntitiesResource, TestValues.TestEntityService, TestValues.TestEntity.apply)

      case req@PUT -> Root / _ if services(req, TestValues.testEntitiesResource) =>
        executeRequest(req, None, TestValues.testEntitiesResource, TestValues.TestEntityService, TestValues.TestEntity.apply)

      case _ =>
        NotFound()

    }
  }

}
