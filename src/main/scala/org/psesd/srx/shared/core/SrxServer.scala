package org.psesd.srx.shared.core

import org.http4s.dsl.{->, /, Root, _}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.{Router, Server, ServerApp}
import org.http4s.util.CaseInsensitiveString
import org.http4s.{HttpService, Request, Response}
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, SifRequestNotAuthorizedException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.logging.LogLevel.LogLevel
import org.psesd.srx.shared.core.logging.{LogLevel, Logger}
import org.psesd.srx.shared.core.sif.SifRequestAction.SifRequestAction
import org.psesd.srx.shared.core.sif._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext
import scala.xml.Node
import scalaz.concurrent.Task

/** SRX Server base.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
trait SrxServer extends ServerApp {

  private final val InternalServerErrorDescription = "Internal server error."
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

  protected def serviceRouter(implicit executionContext: ExecutionContext) = HttpService {
    case req@GET -> Root =>
      Ok()

    case _ -> Root =>
      NotImplemented()

    case req@GET -> Root / _ if services(req, CoreResource.Ping.toString) =>
      Ok(true.toString)

    case req@GET -> Root / _ if services(req, CoreResource.Info.toString) =>
      respondWithInfo(getDefaultSrxResponse(req))
  }

  protected def addRouteParams(param: String*): Option[List[SifRequestParameter]] = {
    val params = ArrayBuffer[SifRequestParameter]()
    for (i <- param.indices by 2) {
      params += SifRequestParameter(param(i), param(i + 1))
    }
    Some(params.toList)
  }

  protected def respondWithInfo(srxResponse: SrxResponse): Task[Response] = {
    if (!srxResponse.hasError) {
      try {
        srxResponse.sifResponse.bodyXml = Option(srxService.toXml)
      } catch {
        case e: Exception =>
          srxResponse.setError(new SifError(
            SifHttpStatusCode.InternalServerError,
            SrxOperation.Info.toString,
            "Unhandled exception retrieving service info.",
            e.getMessage
          ))
      }
    }
    srxResponse.toHttpResponse
  }

  protected def services(httpRequest: Request, resources: String*): Boolean = {
    val resourcePath = new StringBuilder()
    var delimiter = ""
    for(r <- resources) {
      resourcePath.append(delimiter + r.toLowerCase())
      delimiter = "/"
    }
    val path = httpRequest.pathInfo.toLowerCase
    val resource = resourcePath.toString
    path.startsWith("/" + resource + ";") ||
      path.startsWith(resource + ";") ||
      path.startsWith("/" + resource + "/") ||
      path.startsWith(resource + "/") ||
      path.equals("/" + resource) ||
      path.equals(resource)
  }

  protected def getDefaultSrxResponse(httpRequest: Request): SrxResponse = {
    var srxResponse: SrxResponse = null
    try {
      val srxRequest = SrxRequest(sifProvider, httpRequest)
      srxResponse = new SrxResponse(srxRequest)
      srxResponse.sifResponse.sifRequest.receivedUri = Some("%s%s".format(sifProvider.url.toString, httpRequest.pathInfo))
      srxResponse.sifResponse.statusCode = SifHttpStatusCode.Ok
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
      sifRequest.accept = sifRequest.getContentType(getHeaderValue(httpRequest, SifHeader.Accept.toString))
      sifRequest.receivedUri = Some(getRequestUri(httpRequest))
      sifRequest.requestAction = sifRequest.getRequestAction(getHeaderValue(httpRequest, SifHeader.RequestAction.toString), httpRequest.method.name)
      sifRequest.requestId = getHeaderValueOption(httpRequest, SifHeader.RequestId.toString)
      sifRequest.serviceType = SifServiceType.withNameCaseInsensitiveOption(getHeaderValue(httpRequest, SifHeader.ServiceType.toString))
      // add all received header values
      for (h <- httpRequest.headers) {
        sifRequest.addHeader(h.name.value, h.value)
      }
    } catch {
      case _: Throwable =>
    }
    val srxRequest = SrxRequest(sifRequest)
    new SrxResponse(srxRequest)
  }

  protected def getHeaderValueOption(httpRequest: Request, name: String): Option[String] = {
    val value = getHeaderValue(httpRequest, name)
    if (value == null) {
      None
    } else {
      Option(value)
    }
  }

  protected def getHeaderValue(httpRequest: Request, name: String): String = {
    val header = httpRequest.headers.get(CaseInsensitiveString(name)).orNull
    if (header == null) {
      null
    } else {
      header.value
    }
  }

  private def getRequestUri(httpRequest: Request): String = {
    "%s%s".format(sifProvider.url.toString, httpRequest.pathInfo)
  }

  private def setEnvironmentVariables(): Unit = {
    serverApiRoot = Environment.getPropertyOrElse(ServerApiRootKey, "")
    serverHost = Environment.getPropertyOrElse(ServerHostKey, "0.0.0.0")
    serverPort = Environment.getPropertyOrElse(ServerPortAlternateKey, Environment.getPropertyOrElse(ServerPortKey, "8080"))
  }

  private def logServerEvent(event: String, args: List[String]): Unit = {
    val message = getServerEventMessage(event, args)
    Logger.log(LogLevel.Info, message)
    createServerEventMessage(message)
  }

  protected def createServerEventMessage(message: SrxMessage): Unit = {
    SrxMessageService.createMessage(srxService.service.name, message)
  }

  protected def getServerEventMessage(event: String, args: List[String]): SrxMessage = {
    val sb = new StringBuilder("%s server %s on port %s at address %s (apiRoot=%s).".format(event, srxService.service.name, serverPort, serverHost, serverApiRoot))
    if (event == "Starting") {
      sb.append("  ARGS:")
      for (a <- args) {
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

  protected def executeRequest(httpRequest: Request,
                               routeParameters: Option[List[SifRequestParameter]],
                               resourceName: String,
                               service: SrxResourceService
                              ): Task[Response] = {
    executeRequest(httpRequest, routeParameters, resourceName, service, null)
  }

  protected def executeRequest(httpRequest: Request,
                               routeParameters: Option[List[SifRequestParameter]],
                               resourceName: String,
                               service: SrxResourceService,
                               serviceEntity: (SrxRequestBody, Option[List[SifRequestParameter]]) => SrxResource
                              ): Task[Response] = {
    val response = getDefaultSrxResponse(httpRequest)

    val requestAction = SifRequestAction.fromHttpMethod(SifHttpRequestMethod.withNameCaseInsensitive(httpRequest.method.name))

    if (!response.hasError) {
      try {
        val zoneId = response.srxRequest.sifRequest.zone.toString
        val contextId = response.srxRequest.sifRequest.context.toString
        val requestParameters = getRequestParameters(httpRequest, routeParameters, resourceName, zoneId, contextId)
        var resource: SrxResource = null
        var resourceErrorResult: SrxResourceErrorResult = null

        val result = requestAction match {

          case SifRequestAction.Delete =>
            service.delete(requestParameters)

          case SifRequestAction.Create =>
            try {
              resource = serviceEntity(new SrxRequestBody(response.srxRequest), Some(requestParameters))
            } catch {
              case e: Exception =>
                resourceErrorResult = SrxResourceErrorResult(SifHttpStatusCode.BadRequest, e)
            }
            if(resourceErrorResult != null) {
              resourceErrorResult
            } else {
              service.create(resource, requestParameters)
            }

          case SifRequestAction.Query =>
            service.query(requestParameters)

          case SifRequestAction.Update =>
            try {
              resource = serviceEntity(new SrxRequestBody(response.srxRequest), Some(requestParameters))
            } catch {
              case e: Exception =>
                resourceErrorResult = SrxResourceErrorResult(SifHttpStatusCode.BadRequest, e)
            }
            if(resourceErrorResult != null) {
              resourceErrorResult
            } else {
              service.update(resource, requestParameters)
            }

          case _ =>
            throw new ArgumentInvalidException("requestAction")
        }

        // always respond with status code set by service result
        response.sifResponse.statusCode = result.statusCode

        if(result.success) {
          // only set body if result = success
          if(response.srxRequest.accepts.equals(SifContentType.Json)) {
            response.sifResponse.bodyJson = result.toJson
            // if response does not implement an explicit JSON serializer, default to XML for auto-serialization
            if(response.sifResponse.bodyJson.isEmpty) {
              response.sifResponse.bodyXml = result.toXml
            }
          } else {
            response.sifResponse.bodyXml = result.toXml
          }
        } else {
          // if internal server error, return a generic response description and log the actual error + stack trace
          if(result.statusCode.equals(SifHttpStatusCode.InternalServerError)) {
            response.setError(new SifError(
              result.statusCode,
              resourceName,
              getResourceErrorTitle(requestAction, resourceName),
              InternalServerErrorDescription
            ))
          } else {
            // for all other errors return the message set by service result
            response.setError(new SifError(
              result.statusCode,
              resourceName,
              getResourceErrorTitle(requestAction, resourceName),
              result.exceptions.head.getMessage
            ))
          }
          logError(LogLevel.Debug, requestAction, resourceName, response.srxRequest, result.exceptions.head)
        }
      } catch {
        case e: Exception =>
          // any exception here is an unhandled server error, so return generic response and log actual error + stack trace
          response.setError(new SifError(
            SifHttpStatusCode.InternalServerError,
            resourceName,
            getResourceErrorTitle(requestAction, resourceName),
            InternalServerErrorDescription
          ))
          logError(LogLevel.Error, requestAction, resourceName, response.srxRequest, e)
      }
    } else {
      logError(LogLevel.Debug, requestAction, resourceName, response.srxRequest, new Exception(response.sifResponse.error.get.description))
    }

    response.toHttpResponse
  }

  private def getHeaderParameter(httpRequest: Request, parameterName: String): Option[SifRequestParameter] = {
    val header = httpRequest.headers.find(h => h.name.value.toLowerCase() == parameterName.toLowerCase())
    if (header.isDefined) {
      Some(SifRequestParameter(parameterName, header.get.value))
    } else {
      None
    }
  }

  private def getResourceErrorTitle(requestAction: SifRequestAction, resourceName: String): String = {
    "Failed to %s %s.".format(requestAction.toString.toLowerCase, resourceName)
  }

  private def getRequestParameters(
                                    httpRequest: Request,
                                    routeParameters: Option[List[SifRequestParameter]],
                                    resourceName: String,
                                    zoneId: String,
                                    contextId: String
                                  ): List[SifRequestParameter] = {
    val parameters = new ArrayBuffer[SifRequestParameter]()
    parameters += SifRequestParameter("zoneId", zoneId)
    parameters += SifRequestParameter("contextId", contextId)
    if(routeParameters.isDefined) {
      for(p <- routeParameters.get) {
        parameters += p
      }
    }
    try {
      val resourceId = getResourceId(httpRequest, resourceName)
      if (resourceId.isDefined) {
        parameters += SifRequestParameter("id", resourceId.get)
      }

      val responseFormat = getHeaderParameter(httpRequest, "ResponseFormat")
      if (responseFormat.isDefined) {
        parameters += responseFormat.get
      }

      val generatorId = getHeaderParameter(httpRequest, SifHeader.GeneratorId.toString())
      if (generatorId.isDefined) {
        parameters += generatorId.get
      }

      val requestId = getHeaderParameter(httpRequest, SifHeader.RequestId.toString())
      if (requestId.isDefined) {
        parameters += requestId.get
      }

      val sourceIp = getHeaderParameter(httpRequest, SifHttpHeader.ForwardedFor.toString())
      if (sourceIp.isDefined) {
        parameters += sourceIp.get
      }

      val userAgent = getHeaderParameter(httpRequest, SifHttpHeader.UserAgent.toString())
      if (userAgent.isDefined) {
        parameters += userAgent.get
      }

      parameters += SifRequestParameter("uri", getRequestUri(httpRequest))

      val queryString = httpRequest.queryString
      if (!queryString.isNullOrEmpty) {
        val pairs = queryString.split("&")
        if (pairs.nonEmpty) {
          for (pair <- pairs) {
            val kv = pair.split("=")
            if (kv.length == 2 && (!(kv(0).toLowerCase == "id") || resourceId.isEmpty)) {
              parameters += SifRequestParameter(kv(0), kv(1))
            }
          }
        }
      }
    } catch {
      case e: Exception =>
        throw new ArgumentInvalidException("request query string")
    }
    parameters.toList
  }

  private def getResourceId(httpRequest: Request, resourceName: String): Option[String] = {
    try {
      val segments = httpRequest.pathInfo.split("/")
      if (segments.nonEmpty) {
        var resourcePath = ""
        for (i <- 0 until segments.length) {
          if (resourcePath.isNullOrEmpty && segments(i).toLowerCase == resourceName.toLowerCase) {
            resourcePath = segments(i + 1)
          }
        }
        if (resourcePath.contains(";")) {
          val r = resourcePath.split(";")
          if (r.nonEmpty && !r(0).isNullOrEmpty) {
            Some(r(0))
          } else {
            None
          }
        } else {
          if(resourcePath.isNullOrEmpty) {
            None
          } else {
            Some(resourcePath)
          }
        }
      } else {
        None
      }
    } catch {
      case e: Exception =>
        throw new ArgumentInvalidException("resource id")
    }
  }

  private def logError(logLevel: LogLevel, requestAction: SifRequestAction, resourceName: String, srxRequest: SrxRequest, e: Exception): Unit = {
    if(e != null) {
      srxRequest.errorMessage = Some(e.getMessage)
      srxRequest.errorStackTrace = Some(e.getFormattedStackTrace)
    }
    Logger.log(logLevel, SrxMessage(
      srxService,
      Some(resourceName),
      None,
      None,
      getResourceErrorTitle(requestAction, resourceName),
      Some(srxRequest)
    ))
  }

}
