package org.psesd.srx.shared.core.logging

import org.psesd.srx.shared.core.config.Environment

/** Provides logging functions shared by SRX components and services.
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

  def log(level: LogLevel, message: String) = {
    level match {
      case Debug =>
        if(logLevel == LogLevel.Debug) {
          logger.debug(message)
        }

      case Info =>
        if(logLevel == LogLevel.Debug
          || logLevel == LogLevel.Info) {
          logger.info(message)
        }

      case Warning =>
        if(logLevel == LogLevel.Debug
          || logLevel == LogLevel.Info
          || logLevel == LogLevel.Warning) {
          logger.warn(message)
        }

      case Error =>
        logger.error(message)
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
