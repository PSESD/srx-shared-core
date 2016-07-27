package org.psesd.srx.shared.core

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.psesd.srx.shared.core.config.Environment
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
    var actual = ""
    tempServer onComplete {
      case Success(x) =>
        assert(!actual.isEmpty)
      case _ =>
    }

    // wait for server to init
    Thread.sleep(2000)

    // ping server and collect response
    val httpclient: CloseableHttpClient = HttpClients.custom().disableCookieManagement().build()
    val httpGet = new HttpGet("http://localhost:%s/info".format(Environment.getPropertyOrElse("SERVER_PORT", "80")))
    val response = httpclient.execute(httpGet)
    actual = EntityUtils.toString(response.getEntity)

    // start server
    Await.result(tempServer, AwaitResultDuration)
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

  private object srxServer extends SrxServer {
    def srxService = TestValues.srxService
  }

}
