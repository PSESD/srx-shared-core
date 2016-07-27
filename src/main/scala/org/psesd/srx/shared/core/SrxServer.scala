package org.psesd.srx.shared.core

import org.http4s.dsl.{->, /, Root, _}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.{Router, ServerApp}
import org.http4s.util.CaseInsensitiveString
import org.http4s.{Header, HttpService}
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.concurrent.ExecutionContext

/** SRX Server base.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
trait SrxServer extends ServerApp {

  private final val ServerApiRootKey = "SERVER_API_ROOT"
  private final val ServerPortKey = "SERVER_PORT"

  private val serverApiRoot = Environment.getPropertyOrElse(ServerApiRootKey, "")

  def srxService: SrxService

  def server(args: List[String]) = BlazeBuilder
    .bindHttp(Environment.getProperty(ServerPortKey).toInt)
    .mountService(service, serverApiRoot)
    .start

  def service(implicit executionContext: ExecutionContext = ExecutionContext.global): HttpService = Router(
    "" -> rootService
  )

  def rootService(implicit executionContext: ExecutionContext) = HttpService {

    case req@GET -> Root =>
      Ok()

    case _ -> Root =>
      NotImplemented()

    case GET -> Root / "ping" =>
      Ok(true.toString)

    case req@GET -> Root / "info" =>
      Ok(getInfo(req.headers.get(CaseInsensitiveString("accept"))))
  }

  def getInfo(accept: Option[Header]): String = {
    if (accept.isEmpty || accept.orNull.value.toLowerCase.equals("xml")) {
      srxService.toXml.toXmlString
    } else {
      srxService.toXml.toJsonString
    }
  }

}
