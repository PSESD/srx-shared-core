package org.psesd.srx.shared.core

import java.util.UUID

import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.sif._
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Success

class SrxServerTests extends FunSuite {

  private final val ServerDuration = 5000
  private final val AwaitResultDuration = 6 seconds

  private val pendingInterrupts = new ThreadLocal[List[Thread]] {
    override def initialValue = Nil
  }

  private val tempServer = Future {
    delayedInterrupt(ServerDuration)
    intercept[InterruptedException] {
      startServer()
    }
  }

  ignore("ping") {
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

    // start server
    Await.result(tempServer, AwaitResultDuration)
  }

  ignore("info") {
    // execute SIF request
    val sifRequest = new SifRequest(TestValues.sifProvider, "info")
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_OK))
    assert(responseBody.contains("<service>"))

    // start server
    Await.result(tempServer, AwaitResultDuration)
  }

  ignore("info with invalid session token") {
    // execute SIF request
    val invalidProvider = new SifProvider(TestValues.sifUrl, SifProviderSessionToken(UUID.randomUUID.toString), TestValues.sharedSecret, TestValues.sifAuthenticationMethod)
    val sifRequest = new SifRequest(invalidProvider, "info")
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_UNAUTHORIZED))
    assert(responseBody.contains("<scope>Info</scope>"))
    assert(responseBody.contains("<message>Unauthorized</message>"))
    assert(responseBody.contains("<description>SIF user or session '"))

    // start server
    Await.result(tempServer, AwaitResultDuration)
  }

  ignore("info with invalid shared secret") {
    // execute SIF request
    val invalidProvider = new SifProvider(TestValues.sifUrl, TestValues.sessionToken, SifProviderSharedSecret("invalid"), TestValues.sifAuthenticationMethod)
    val sifRequest = new SifRequest(invalidProvider, "info")
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_UNAUTHORIZED))
    assert(responseBody.contains("<scope>Info</scope>"))
    assert(responseBody.contains("<message>Unauthorized</message>"))
    assert(responseBody.contains("<description>The authorization parameter is invalid.</description>"))

    // start server
    Await.result(tempServer, AwaitResultDuration)
  }

  ignore("info with invalid authentication method") {
    // execute SIF request
    val invalidProvider = new SifProvider(TestValues.sifUrl, TestValues.sessionToken, TestValues.sharedSecret, SifAuthenticationMethod.Basic)
    val sifRequest = new SifRequest(invalidProvider, "info")
    val response = new SifConsumer().query(sifRequest)
    val responseBody = response.body.getOrElse("")
    assert(response.statusCode.equals(HttpStatus.SC_UNAUTHORIZED))
    assert(responseBody.contains("<scope>Info</scope>"))
    assert(responseBody.contains("<message>Unauthorized</message>"))
    assert(responseBody.contains("<description>SIF authentication method 'Basic' is invalid.</description>"))

    // start server
    Await.result(tempServer, AwaitResultDuration)
  }

  //new SifProvider(sifUrl, sessionToken, sharedSecret, sifAuthenticationMethod)

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

  private object srxServer extends SrxServer {
    def srxService = TestValues.srxService

    def sifProvider = TestValues.sifProvider
  }

}
