package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core.{SrxMessage, SrxService}
import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions._

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

  def log(level: LogLevel, message: String, description: String, service: SrxService): Unit = {
    try {
      if (level == null) {
        throw new ArgumentNullException("level parameter")
      }

      if (message == null || message.isEmpty) {
        throw new ArgumentNullOrEmptyOrWhitespaceException("message parameter")
      }

      if (description == null) {
        throw new ArgumentNullException("description parameter")
      }

      val srxMessage = SrxMessage(service, message)
      srxMessage.body = Some(description)

      log(level, srxMessage)
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
    }
  }

  def log(level: LogLevel, srxMessage: SrxMessage): Unit = {
    try {
      if (level == null) {
        throw new ArgumentNullException("level parameter")
      }

      if (srxMessage == null) {
        throw new ArgumentNullException("srxMessage parameter")
      }

      level match {
        case Local =>
          if (logLevel == LogLevel.Local) {
            logger.debug(srxMessage.description)
          }

        case Debug =>
          if (logLevel == LogLevel.Debug) {
            logger.debug(srxMessage.description)
            sendToRollbar(logLevel, srxMessage)
          }

        case Info =>
          if (logLevel == LogLevel.Debug
            || logLevel == LogLevel.Info) {
            logger.info(srxMessage.description)
            sendToRollbar(logLevel, srxMessage)
          }

        case Warning =>
          if (logLevel == LogLevel.Debug
            || logLevel == LogLevel.Info
            || logLevel == LogLevel.Warning) {
            logger.warn(srxMessage.description)
            sendToRollbar(logLevel, srxMessage)
          }

        case Error =>
          if (logLevel == LogLevel.Debug
            || logLevel == LogLevel.Info
            || logLevel == LogLevel.Warning
            || logLevel == LogLevel.Error) {
            logger.error(srxMessage.description)
            sendToRollbar(logLevel, srxMessage)
          }

        case Critical =>
          if (logLevel == LogLevel.Debug
            || logLevel == LogLevel.Info
            || logLevel == LogLevel.Warning
            || logLevel == LogLevel.Error
            || logLevel == LogLevel.Critical) {
            logger.error(srxMessage.description)
            sendToRollbar(logLevel, srxMessage)
          }
      }
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
    }
  }

  private def sendToRollbar(level: LogLevel, srxMessage: SrxMessage): Unit = {
    val result = RollbarClient.SendItem(new RollbarMessage(srxMessage, level).getJsonString())
    result match {
      case 200 =>

      case 401 =>
        throw new RollbarUnauthorizedException()

      case 404 =>
        throw new RollbarNotFoundException()

      case _ =>
        throw new RollbarUnhandledException(result)
    }
  }

  private def getLogger: Logger = {
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel.toString.toUpperCase)
    LoggerFactory.getLogger(LoggerName)
  }

  private def getLogLevel: LogLevel = {
    LogLevel.withNameCaseInsensitive(Environment.getProperty(LogLevelKey))
  }
}
