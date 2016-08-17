package org.psesd.srx.shared.core.sif

/** Enumeration of HTTP status codes.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifHttpStatusCode {
  final val Ok = 200
  final val Created = 201
  final val Accepted = 202
  final val BadRequest = 400
  final val Unauthorized = 401
  final val Forbidden = 403
  final val NotFound = 404
  final val MethodNotAllowed = 405
  final val InternalServerError = 500
  final val NotImplemented = 501
}
