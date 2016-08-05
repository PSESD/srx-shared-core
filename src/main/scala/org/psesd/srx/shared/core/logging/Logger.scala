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
  private var logger: Logger = _

  def init(c: Class[_]): Unit = {
    logger = getLogger(c)
  }

  def log(level: LogLevel, subject: String, description: String, service: SrxService): Unit = {
    try {
      if (level == null) {
        throw new ArgumentNullException("level parameter")
      }

      if (subject == null || subject.isEmpty) {
        throw new ArgumentNullOrEmptyOrWhitespaceException("message parameter")
      }

      if (description == null) {
        throw new ArgumentNullException("description parameter")
      }

      val srxMessage = SrxMessage(service, subject)
      srxMessage.body = Some(description)

      log(level, srxMessage)
    } catch {
      case e: Exception =>
        if(logger == null) {
          println(e.getMessage)
        } else {
          logger.error(e.getMessage)
        }
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

      val logMessage: String = getLogMessage(srxMessage)

      level match {
        case Local =>
          if (logLevel == LogLevel.Local) {
            if(logger == null) {
              println(logMessage)
            } else {
              logger.debug(logMessage)
            }
          }

        case Debug =>
          if (logLevel == LogLevel.Debug) {
            if(logger == null) {
              println(logMessage)
            } else {
              logger.debug(logMessage)
            }
            sendToRollbar(logLevel, srxMessage)
          }

        case Info =>
          if (logLevel == LogLevel.Debug
            || logLevel == LogLevel.Info) {
            if(logger == null) {
              println(logMessage)
            } else {
              logger.info(logMessage)
            }
            sendToRollbar(logLevel, srxMessage)
          }

        case Warning =>
          if (logLevel == LogLevel.Debug
            || logLevel == LogLevel.Info
            || logLevel == LogLevel.Warning) {
            if(logger == null) {
              println(logMessage)
            } else {
              logger.warn(logMessage)
            }
            sendToRollbar(logLevel, srxMessage)
          }

        case Error =>
          if (logLevel == LogLevel.Debug
            || logLevel == LogLevel.Info
            || logLevel == LogLevel.Warning
            || logLevel == LogLevel.Error) {
            if(logger == null) {
              println(logMessage)
            } else {
              logger.error(logMessage)
            }
            sendToRollbar(logLevel, srxMessage)
          }

        case Critical =>
          if (logLevel == LogLevel.Debug
            || logLevel == LogLevel.Info
            || logLevel == LogLevel.Warning
            || logLevel == LogLevel.Error
            || logLevel == LogLevel.Critical) {
            if(logger == null) {
              println(logMessage)
            } else {
              logger.error(logMessage)
            }
            sendToRollbar(logLevel, srxMessage)
          }
      }
    } catch {
      case e: Exception =>
        if(logger == null) {
          println(e.getMessage)
        } else {
          logger.error(e.getMessage)
        }
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

  private def getLogger(c: Class[_]): Logger = {
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel.toString.toUpperCase)
    LoggerFactory.getLogger(c)
  }

  private def getLogLevel: LogLevel = {
    LogLevel.withNameCaseInsensitive(Environment.getProperty(LogLevelKey))
  }

  private def getLogMessage(srxMessage: SrxMessage): String = {
    val subject: String = {
      if(srxMessage.description.endsWith(".")) {
        srxMessage.description
      } else {
        srxMessage.description + "."
      }
    }
    val description: String = {
      if(srxMessage.body.isDefined) {
        " " + srxMessage.body.get
      } else {
        ""
      }
    }
    srxMessage.srxService.service.name + " v" + srxMessage.srxService.service.version + ": " + subject + description
  }
}
