package org.psesd.srx.shared.core

import org.http4s.dsl.{->, /, Root, _}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.{Router, Server, ServerApp}
import org.http4s.{HttpService, Request}
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.SifRequestNotAuthorizedException
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.logging.{LogLevel, Logger}
import org.psesd.srx.shared.core.sif._

import scala.concurrent.ExecutionContext
import scalaz.concurrent.Task

/** SRX Server base.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
trait SrxServer extends ServerApp {

  private final val ServerApiRootKey = "SERVER_API_ROOT"
  private final val ServerHostKey = "SERVER_HOST"
  private final val ServerPortKey = "SERVER_PORT"
  private final val ServerPortAlternateKey = "PORT"

  private var serverApiRoot: String = _
  private var serverHost: String = _
  private var serverPort: String = _


  def sifProvider: SifProvider

  def srxService: SrxService

  def server(args: List[String]): Task[Server] = {
    try {
      setEnvironmentVariables()

      logServerEvent("Starting", args)

      BlazeBuilder
        .bindHttp(serverPort.toInt, serverHost)
        .mountService(service, serverApiRoot)
        .start
    } catch {
      case e: Exception =>
        Logger.log(LogLevel.Error, e.getMessage, e.getFormattedStackTrace, srxService)
        null
    }
  }

  def service(implicit executionContext: ExecutionContext = ExecutionContext.global): HttpService = Router(
    "" -> serviceRouter
  )

  override def shutdown(server: Server): Task[Unit] = {
    try {
      logServerEvent("Stopping", List[String]())

      server.shutdown
    } catch {
      case e: Exception =>
        Logger.log(LogLevel.Error, e.getMessage, e.getFormattedStackTrace, srxService)
        null
    }
  }

  protected def serviceRouter(implicit executionContext: ExecutionContext) = HttpService {

    case req@GET -> Root =>
      Ok()

    case _ -> Root =>
      NotImplemented()

    case GET -> Root / "ping" =>
      Ok(true.toString)

    case req@GET -> Root / _ if req.pathInfo.startsWith("/info") =>
      respondWithInfo(getDefaultSrxResponse(req)).toHttpResponse

  }

  protected def createServerEventMessage(message: SrxMessage): Unit = {
    SrxMessageService.createMessage(srxService.service.name, message)
  }

  protected def getDefaultSrxResponse(httpRequest: Request): SrxResponse = {
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
      sifRequest.requestAction = SrxRequest.getRequestAction(httpRequest)
      sifRequest.requestId = SrxRequest.getHeaderValueOption(httpRequest, SifHeader.RequestId.toString)
      sifRequest.serviceType = SifServiceType.withNameCaseInsensitiveOption(SrxRequest.getHeaderValue(httpRequest, SifHeader.ServiceType.toString))
    } catch {
      case _: Throwable =>
    }
    val srxRequest = SrxRequest(sifRequest)
    new SrxResponse(srxRequest)
  }

  protected def getServerEventMessage(event: String, args: List[String]): SrxMessage = {
    val sb = new StringBuilder("%s server %s on port %s at address %s (apiRoot=%s).".format(event, srxService.service.name, serverPort, serverHost, serverApiRoot))
    if(event == "Starting") {
      sb.append("  ARGS:")
      for(a <- args) {
        sb.append(" " + a + ";")
      }
      val Undefined = "[UNDEFINED]"
      sb.append("  ENVIRONMENT: ")
      sb.append(ServerPortAlternateKey + "=" + Environment.getPropertyOrElse(ServerPortAlternateKey, Undefined) + "; ")
      sb.append(ServerApiRootKey + "=" + Environment.getPropertyOrElse(ServerApiRootKey, Undefined) + "; ")
      sb.append(ServerHostKey + "=" + Environment.getPropertyOrElse(ServerHostKey, Undefined) + "; ")
      sb.append(ServerPortKey + "=" + Environment.getPropertyOrElse(ServerPortKey, Undefined) + ";")
    }

    val message = SrxMessage(srxService, "%s SRX server.".format(event))
    message.body = Some(sb.toString)
    message
  }

  protected def respondWithInfo(srxResponse: SrxResponse): SrxResponse = {
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

  private def logServerEvent(event: String, args: List[String]): Unit = {
    val message = getServerEventMessage(event, args)
    Logger.log(LogLevel.Info, message)
    createServerEventMessage(message)
  }

  private def setEnvironmentVariables(): Unit = {
    serverApiRoot = Environment.getPropertyOrElse(ServerApiRootKey, "")
    serverHost = Environment.getPropertyOrElse(ServerHostKey, "0.0.0.0")
    serverPort = Environment.getPropertyOrElse(ServerPortAlternateKey, Environment.getPropertyOrElse(ServerPortKey, "8080"))
  }

}
