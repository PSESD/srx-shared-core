package org.psesd.srx.shared.core.config

import java.io.{File, FileInputStream}

import org.psesd.srx.shared.core.exceptions.EnvironmentException

/** Provides environment variable functions shared by SRX components and services.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
object Environment {
  final val Development = "development"
  final val Local = "local"

  private final val EnvironmentKey = "ENVIRONMENT"
  private final val LocalEnvironmentFileName = "env-local.properties"

  private var envName: String = null
  private var properties: java.util.Properties = null

  val name: String = {
    if(envName == null) {
      envName = getProperty(EnvironmentKey)
    }
    envName
  }

  def getProperty(key: String): String = {
    val result = getPropertyOrElse(key, null)
    if (result == null || result.isEmpty) {
      throw new EnvironmentException("Missing environment variable '%s'.".format(key))
    }
    result
  }

  def getPropertyOrElse(key: String, default: String): String = {
    var result = sys.env.getOrElse(key, null)
    if (result == null && (envName == null || envName == Local)) {
      result = getFileProperty(key)
    }
    if (result == null || result.isEmpty) {
      result = default
    }
    result
  }

  private def getFileProperty(key: String): String = {
    if (properties == null) {
      loadProperties(LocalEnvironmentFileName)
    }
    properties.getProperty(key, null)
  }

  private def loadProperties(fileName: String): Unit = {
    properties = new java.util.Properties()
    val propertiesFile = new File(fileName)
    if(!propertiesFile.exists()) {
      throw new EnvironmentException("Local environment file '%s' does not exist in project root directory.".format(LocalEnvironmentFileName))
    }
    val propertiesFileStream = new FileInputStream(propertiesFile)
    properties.load(propertiesFileStream)
    propertiesFileStream.close()
  }

}
