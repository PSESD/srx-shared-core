package org.psesd.srx.shared.core

import org.http4s.dsl.{->, /, Root, _}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.{Router, ServerApp}
import org.http4s.{HttpService, Request}
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.SifRequestNotAuthorizedException
import org.psesd.srx.shared.core.sif._

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

  def sifProvider: SifProvider

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

    case req@GET -> Root / _ if req.pathInfo.startsWith("/info") =>
      respondWithInfo(getDefaultSrxResponse(req)).toHttpResponse

  }

  def getDefaultSrxResponse(httpRequest: Request): SrxResponse = {
    var srxResponse: SrxResponse = null
    try {
      val srxRequest = SrxRequest(sifProvider, httpRequest)
      srxResponse = new SrxResponse(srxRequest)
      srxResponse.sifResponse.statusCode = Ok.code
    } catch {
      case ae: SifRequestNotAuthorizedException =>
        srxResponse = getErrorSrxResponse(httpRequest)
        srxResponse.setError(new SifError(
          Unauthorized.code,
          SrxOperation.Info.toString,
          Unauthorized.reason,
          ae.getMessage
        ))

      case e: Exception =>
        srxResponse = getErrorSrxResponse(httpRequest)
        srxResponse.setError(new SifError(
          BadRequest.code,
          SrxOperation.Info.toString,
          BadRequest.reason,
          e.getMessage
        ))
    }
    srxResponse
  }

  private def getErrorSrxResponse(httpRequest: Request): SrxResponse = {
    val sifRequest = new SifRequest(sifProvider, "", SifZone(), SifContext(), SifTimestamp())
    try {
      sifRequest.accept = SrxRequest.getAccept(httpRequest)
    } catch {
      case _ : Throwable =>
    }
    val srxRequest = SrxRequest(sifRequest)
    new SrxResponse(srxRequest)
  }

  private def respondWithInfo(srxResponse: SrxResponse): SrxResponse = {
    if (!srxResponse.hasError) {
      try {
        srxResponse.sifResponse.bodyXml = Option(srxService.toXml)
      } catch {
        case e: Exception =>
          srxResponse.setError(new SifError(
            InternalServerError.code,
            SrxOperation.Info.toString,
            "Unhandled exception retrieving service info.",
            e.getMessage
          ))
      }
    }
    srxResponse
  }

}
