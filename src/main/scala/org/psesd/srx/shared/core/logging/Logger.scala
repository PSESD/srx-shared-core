package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core.SrxMessage
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, RollbarNotFoundException, RollbarUnauthorizedException}
import org.psesd.srx.shared.core.sif.{SifMessageId, SifTimestamp}

/** Logging functions shared by SRX components and services.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
object Logger {

  import LogLevel._
  import org.slf4j.{Logger, LoggerFactory}

  private final val LoggerName = "Logger"
  private final val LogLevelKey = "LOG_LEVEL"

  private final val logLevel = getLogLevel
  private final val logger = getLogger

  def log(level: LogLevel, message: String): Unit = {
    if (level == null) {
      throw new ArgumentNullException("level parameter")
    }

    if (message == null || message.isEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("message parameter")
    }

    val srxMessage = SrxMessage(
      Option(SifMessageId()),
      SifTimestamp(),
      None,
      None,
      None,
      None,
      Option(message),
      None,
      None,
      None,
      None
    )

    log(level, srxMessage)
  }

  def log(level: LogLevel, srxMessage: SrxMessage): Unit = {
    if (level == null) {
      throw new ArgumentNullException("level parameter")
    }

    if (srxMessage == null) {
      throw new ArgumentNullException("srxMessage parameter")
    }

    val description = srxMessage.description.orNull
    if (description == null || description.isEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("srxMessage.description parameter")
    }

    level match {
      case Local =>
        if (logLevel == LogLevel.Local) {
          logger.debug(description)
        }

      case Debug =>
        if (logLevel == LogLevel.Debug) {
          logger.debug(description)
          sendToRollbar(logLevel, srxMessage)
        }

      case Info =>
        if (logLevel == LogLevel.Debug
          || logLevel == LogLevel.Info) {
          logger.info(description)
          sendToRollbar(logLevel, srxMessage)
        }

      case Warning =>
        if (logLevel == LogLevel.Debug
          || logLevel == LogLevel.Info
          || logLevel == LogLevel.Warning) {
          logger.warn(description)
          sendToRollbar(logLevel, srxMessage)
        }

      case Error =>
        if (logLevel == LogLevel.Debug
          || logLevel == LogLevel.Info
          || logLevel == LogLevel.Warning
          || logLevel == LogLevel.Error) {
          logger.error(description)
          sendToRollbar(logLevel, srxMessage)
        }

      case Critical =>
        if (logLevel == LogLevel.Debug
          || logLevel == LogLevel.Info
          || logLevel == LogLevel.Warning
          || logLevel == LogLevel.Error
          || logLevel == LogLevel.Critical) {
          logger.error(description)
          sendToRollbar(logLevel, srxMessage)
        }
    }
  }

  private def sendToRollbar(level: LogLevel, srxMessage: SrxMessage): Unit = {
    val result = RollbarClient.SendItem(new RollbarMessage(srxMessage, level).getJsonString())
    result match {
      case 401 =>
        throw new RollbarUnauthorizedException()

      case 404 =>
        throw new RollbarNotFoundException()

      case _ =>
    }
  }

  private def getLogger: Logger = {
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel.toString.toUpperCase)
    LoggerFactory.getLogger(LoggerName)
  }

  private def getLogLevel: LogLevel = {
    LogLevel.withName(Environment.getProperty(LogLevelKey))
  }
}
