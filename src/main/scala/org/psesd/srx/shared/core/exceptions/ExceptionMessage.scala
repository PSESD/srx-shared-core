package org.psesd.srx.shared.core.exceptions

/** Enumeration of common exception messages.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object ExceptionMessage {
  final val AmazonS3Unauthorized = "Amazon S3 connection failed with 403: Forbidden. Check Amazon S3 configuration or environment variables."
  final val IsInvalid = "The %s is invalid."
  final val NotNull = "The %s cannot be null."
  final val NotNullOrEmpty = "The %s cannot be null or empty."
  final val NotNullOrEmptyOrWhitespace = "The %s cannot be null, empty, or whitespace."
  final val RollbarNotFound = "Rollbar message send failed with 404: Not Found. Check 'ROLLBAR_URL' environment variable."
  final val RollbarUnauthorized = "Rollbar message send failed with 401: Unauthorized. Check 'ROLLBAR_ACCESS_TOKEN' environment variable."
  final val RollbarUnhandled = "Rollbar message send failed with code %s."
  final val SifAuthenticationMethodInvalid = "SIF authentication method '%s' is invalid."
  final val SifHeaderInvalid = "SIF header '%s' contains invalid value '%s'."
  final val SifProviderNotAuthorized = "SIF user or session '%s' not authorized."
  final val SrxRequestActionNotAllowed = "The %s action for %s is not allowed."
  final val SrxResourceNotFound = "The requested %s resource was not found."
}