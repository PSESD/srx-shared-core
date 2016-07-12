package org.psesd.srx.shared.core.sif

import java.net.URI

import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Represents a SIF-specific URI.
  *
  * @version 1.0
  * @since 1.0
  * @author David S. Dennison (iTrellis, LLC)
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifUri {
  def apply(sifUri: String): SifUri = new SifUri(sifUri)

  def isValid(sifUri: String): Boolean = {
    if (sifUri.isNullOrEmpty) {
      false
    } else {
      try {
        val check = URI.create(sifUri.trim)
        !check.getScheme.isNullOrEmpty &&
          !check.getHost.isNullOrEmpty
      } catch {
        case _: Throwable => false
      }
    }
  }
}

class SifUri(sifUri: String) {
  if (sifUri.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("sifUri parameter")
  }
  if (!SifUri.isValid(sifUri)) {
    throw new ArgumentInvalidException("sifUri parameter")
  }

  /** Holds the URI. **/
  private val uri = URI.create(sifUri)

  /** The URI scheme name. **/
  val scheme: String = uri.getScheme

  /** The URI host name. **/
  val host: String = uri.getHost

  /** Holds the URI path segments. **/
  private val path: Array[String] = uri.getPath.split('/')

  /** Holds the matrix parameters.
    *
    * @note If the matrix parameter key (lowercased) is '''not''' ''zoneid'' or ''contextid''
    *       then the corresponding value is `None`.
    **/
  private val matrixParams: MatrixParams = {

    val params: Map[String, String] = path.last.split(';').drop(1).foldLeft(Map[String, String]()) {
      (map, pair) =>

        val split = pair.split('=')
        split.length match {
          case 2 => map + (split.head.toLowerCase -> split.last)
          case _ => map
        }
    }

    new MatrixParams(
      if (params.contains(SifMatrixParameter.ZoneId.toString.toLowerCase)) Option(params(SifMatrixParameter.ZoneId.toString.toLowerCase)) else None,
      if (params.contains(SifMatrixParameter.ContextId.toString.toLowerCase)) Option(params(SifMatrixParameter.ContextId.toString.toLowerCase)) else None
    )
  }

  /** The HostedZone zone ID value or `None`. **/
  val zoneId: Option[String] = matrixParams.zoneId

  /** The HostedZone context ID value or `None`. **/
  val contextId: Option[String] = matrixParams.contextId

  private val pathParams: PathParams = {

    val params: List[String] = path.drop(1).foldLeft(List[String]()) { (list, value) =>
      if (value.contains(";")) list :+ value.split(';').head else list :+ value
    }

    new PathParams(
      if (params.headOption.isEmpty) None else Option(params.headOption.orNull),
      if (params.length < 2 || params.drop(1).headOption.isEmpty) None else Option(params.drop(1).headOption.orNull)
    )
  }

  /** The SIF service name or `None`. **/
  val service: Option[String] = pathParams.service

  /** The SIF service object name or `None`. **/
  val serviceObject: Option[String] = pathParams.serviceObject

  /** Holds parsed matrix parameter values.
    *
    * @param zoneId    a zone ID value or `None`.
    * @param contextId a context ID value or `None`.
    **/
  private case class MatrixParams(zoneId: Option[String], contextId: Option[String])

  /** Holds parsed path parameter values (''service'' and ''service object'').
    *
    * @param service       the ''service'' URI segment or `None`.
    * @param serviceObject the ''service object'' URI segment or `None`.
    **/
  private case class PathParams(service: Option[String], serviceObject: Option[String])

}
