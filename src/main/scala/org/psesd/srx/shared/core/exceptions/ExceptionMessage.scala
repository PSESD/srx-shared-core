package org.psesd.srx.shared.core.exceptions

/** Enumeration of common exception messages.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object ExceptionMessage {
  final val IsInvalid = "The %s is invalid."
  final val NotNull = "The %s cannot be null."
  final val NotNullOrEmpty = "The %s cannot be null or empty."
  final val NotNullOrEmptyOrWhitespace = "The %s cannot be null, empty, or whitespace."
  final val RollbarNotFound = "Rollbar message send failed with 404: Not Found. Check 'ROLLBAR_URL' environment variable."
  final val RollbarUnauthorized = "Rollbar message send failed with 401: Unauthorized. Check 'ROLLBAR_ACCESS_TOKEN' environment variable."
  final val RollbarUnhandled = "Rollbar message send failed with code %s."
}