package org.psesd.srx.shared.core

import java.util.UUID

import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.http4s.dsl._
import org.http4s.{Method, Request}
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.extensions.HttpTypeExtensions._
import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

class SrxServerTests extends FunSuite {

  private final val ServerDuration = 5000

  private val pendingInterrupts = new ThreadLocal[List[Thread]] {
    override def initialValue = Nil
  }

  private val tempServer = Future {
    delayedInterrupt(ServerDuration)
    intercept[InterruptedException] {
      startServer()
    }
  }

  test("root") {
    val getRoot = Request(Method.GET, uri("/"))
    val task = srxServer.service.run(getRoot)
    val response = task.run
    assert(response.status.code.equals(HttpStatus.SC_OK))
  }

  test("ping") {
    val getPing = Request(Method.GET, uri("/ping"))
    val task = srxServer.service.run(getPing)
    val response = task.run
    val body = response.body.value
    assert(response.status.code.equals(HttpStatus.SC_OK))
    assert(body.equals(true.toString))
  }

  test("ping (localhost)") {
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

  test("info (localhost)") {
    val sifRequest = new SifRequest(TestValues.sifProvider, "info")
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_OK))
    assert(response.contentType.get.equals(SifContentType.Xml))
    assert(responseBody.contains("<service>"))
  }

  test("info json (localhost)") {
    val sifRequest = new SifRequest(TestValues.sifProvider, "info")
    sifRequest.accept = Option(SifContentType.Json)
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_OK))
    assert(response.contentType.get.equals(SifContentType.Json))
    assert(responseBody.contains("\"service\" : {"))
  }

  test("info with invalid session token (localhost)") {
    val invalidProvider = new SifProvider(TestValues.sifUrl, SifProviderSessionToken(UUID.randomUUID.toString), TestValues.sharedSecret, TestValues.sifAuthenticationMethod)
    val sifRequest = new SifRequest(invalidProvider, "info")
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_UNAUTHORIZED))
    assert(responseBody.contains("<scope>Info</scope>"))
    assert(responseBody.contains("<message>Unauthorized</message>"))
    assert(responseBody.contains("<description>SIF user or session '"))
  }

  test("info with invalid shared secret (localhost)") {
    val invalidProvider = new SifProvider(TestValues.sifUrl, TestValues.sessionToken, SifProviderSharedSecret("invalid"), TestValues.sifAuthenticationMethod)
    val sifRequest = new SifRequest(invalidProvider, "info")
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_UNAUTHORIZED))
    assert(responseBody.contains("<scope>Info</scope>"))
    assert(responseBody.contains("<message>Unauthorized</message>"))
    assert(responseBody.contains("<description>The authorization parameter is invalid.</description>"))
  }

  test("info with invalid authentication method (localhost)") {
    val invalidProvider = new SifProvider(TestValues.sifUrl, TestValues.sessionToken, TestValues.sharedSecret, SifAuthenticationMethod.Basic)
    val sifRequest = new SifRequest(invalidProvider, "info")
    val response = new SifConsumer().query(sifRequest)
    // printlnResponse(response)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_UNAUTHORIZED))
    assert(responseBody.contains("<scope>Info</scope>"))
    assert(responseBody.contains("<message>Unauthorized</message>"))
    assert(responseBody.contains("<description>SIF authentication method 'Basic' is invalid.</description>"))
  }

  test("json info with invalid authentication method (localhost)") {
    val invalidProvider = new SifProvider(TestValues.sifUrl, TestValues.sessionToken, TestValues.sharedSecret, SifAuthenticationMethod.Basic)
    val sifRequest = new SifRequest(invalidProvider, "info")
    sifRequest.accept = Option(SifContentType.Json)
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    // printlnResponse(response)
    assert(response.statusCode.equals(HttpStatus.SC_UNAUTHORIZED))
    assert(response.contentType.get.equals(SifContentType.Json))
    assert(responseBody.contains("\"scope\" : \"Info\""))
    assert(responseBody.contains("\"message\" : \"Unauthorized\""))
    assert(responseBody.contains("\"description\" : \"SIF authentication method 'Basic' is invalid.\""))
  }

  test("info (no headers)") {
    val getInfo = Request(Method.GET, uri("/info"))
    val task = srxServer.service.run(getInfo)
    val response = task.run
    val body = response.body.value
    assert(response.status.code.equals(HttpStatus.SC_BAD_REQUEST))
    assert(body.contains("The sifUri is invalid."))
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
    srxServer.main(Array[String]())
  }

  private def printlnResponse(response: SifResponse): Unit = {
    for (header <- response.getHeaders) {
      println("%s=%s".format(header._1, header._2))
    }
    println(response.body.getOrElse(""))
  }

  private object srxServer extends SrxServer {
    def srxService = TestValues.srxService

    def sifProvider = TestValues.sifProvider
  }

}
