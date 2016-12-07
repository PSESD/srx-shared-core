package org.psesd.srx.shared.core.config

import java.net.URI

import org.psesd.srx.shared.core.io.AmazonS3Client
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.xml.NodeSeq

/** SFTP endpoint configuration.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SftpConfig(configXml: NodeSeq) {

  if (configXml == null) {
    throw new ArgumentNullException("configXml")
  }

  val folder = (configXml \ "folder").text
  if (folder.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("folder")
  }

  val privateKeyPath = (configXml \ "privateKeyPath").text
  if (privateKeyPath.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("privateKeyPath")
  }

  val publicKeyPath = (configXml \ "publicKeyPath").text
  if (publicKeyPath.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("publicKeyPath")
  }

  val uriString = (configXml \ "uri").text
  if (uriString.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("uri")
  }

  val uri = getUri

  val protocol = getProtocol
  if (protocol.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("uri protocol")
  }

  val host = getHost
  if (host.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("uri host")
  }

  val path = getPath

  private var privateKey: String = ""
  private var publicKey: String = ""

  loadKeyFiles

  if (privateKey.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("privateKey")
  }

  if (publicKey.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("publicKey")
  }

  private val userInfo = uri.getUserInfo.split(':')
  val user = userInfo.head
  val password = userInfo.tail.head
  val url = {
    if (uriString.contains("misc/local")) {
      "misc/local"
    } else {
      "%s%s@%s%s/%s".format(protocol, user, host, path, folder)
    }
  }

  def getPrivateKey: String = {
    privateKey
  }

  def getPublicKey: String = {
    publicKey
  }

  if (user.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("user")
  }

  if (password.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("passphrase")
  }

  private def getHost = {
    try {
      uri.getHost
    }
    catch {
      case ex: Exception =>
        throw new ArgumentInvalidException("uri")
    }
  }

  private def getPath = {
    try {
      uri.getPath
    }
    catch {
      case ex: Exception =>
        throw new ArgumentInvalidException("uri")
    }
  }

  private def getProtocol = {
    try {
      s"${uri.getScheme.toLowerCase.trim}://"
    }
    catch {
      case ex: Exception =>
        throw new ArgumentInvalidException("uri")
    }
  }

  private def getUri = {
    try {
      URI.create(uriString)
    }
    catch {
      case ex: Exception =>
        throw new ArgumentInvalidException("uri")
    }
  }

  private def loadKeyFiles = {
    try {
      val s3Client = AmazonS3Client()
      privateKey = s3Client.download(privateKeyPath)
      publicKey = s3Client.download(publicKeyPath)
    }
    catch {
      case ex: Exception =>
    }
  }

}
