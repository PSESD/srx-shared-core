package org.psesd.srx.shared.core.io

import java.io.ByteArrayInputStream
import java.nio.charset.{Charset, CodingErrorAction}

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.model.{AmazonS3Exception, ObjectMetadata}
import org.psesd.srx.shared.core.config.{AmazonS3Config, Environment}
import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/** Provides I/O for files stored in Amazon S3.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class AmazonS3Client(val accessKey: String, val secret: String, val socketTimeout: Int, val bucketName: String, val path: String) {
  if (accessKey.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("accessKey")
  }
  if (secret.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("secret")
  }
  if (bucketName.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("bucketName")
  }
  if (path == null) {
    throw new ArgumentNullException("path")
  }

  private val decoder = Charset.forName("UTF-8").newDecoder()
  private val credentials = new BasicAWSCredentials(accessKey, secret)
  private val clientConfig = new ClientConfiguration
  clientConfig.setSocketTimeout(socketTimeout)

  private lazy val connection = new com.amazonaws.services.s3.AmazonS3Client(credentials, clientConfig)

  def this(config: AmazonS3Config) = {
    this(accessKey = config.accessKey,
      secret = config.secret,
      socketTimeout = config.socketTimeout,
      bucketName = config.bucketName,
      path = Some(config.path).getOrElse(""))
  }

  /** * Delete a file in S3
    *
    * @param fileName File's full path
    * @return Boolean to determine success
    */
  def delete(fileName: String): Unit = {
    val objectPath = getObjectPath(fileName)
    getConnection.deleteObject(bucketName, objectPath)
  }

  /** * Download a text file from S3
    *
    * @param fileName Full path of the file to download
    * @return File content
    */
  def download(fileName: String): String = {
    val objectPath = getObjectPath(fileName)
    val fileObject = getConnection.getObject(bucketName, objectPath)
    val file = fileObject.getObjectContent

    decoder.onMalformedInput(CodingErrorAction.REPLACE)
    decoder.onUnmappableCharacter(CodingErrorAction.REPLACE)
    val fileContent = Source.fromInputStream(file)(decoder).mkString

    file.close()
    fileContent
  }

  /** * Check existence of a file in S3
    *
    * @param fileName Full path filename
    * @return Boolean to determine existence
    */
  def fileExists(fileName: String): Boolean = {
    try {
      val objectPath = getObjectPath(fileName)
      getConnection.getObjectMetadata(bucketName, objectPath)
      true

    } catch {
      case s3: AmazonS3Exception =>
        s3.getErrorCode match {
          case "404 Not Found" =>
            false
          case _ =>
            throw (s3)
        }

      case ex: Exception =>
        throw (ex)
    }
  }

  /** * List objects located in a bucket given a path prefix
    *
    * @param path Path prefix
    * @return List of objects within the bucket given the path prefix
    */
  def list(path: String, includeFolders: Boolean): List[String] = {
    var files = new ArrayBuffer[String]
    val objectPath = getObjectPath(path)
    val objectListing = getConnection.listObjects(bucketName, objectPath)
    objectListing.getObjectSummaries.toList.foreach {
      objectSummary =>
        val key = objectSummary.getKey
        // if specified, do not include folders (ending with "/" suffix)
        if (includeFolders || !key.endsWith("/")) {
          files += key
        }
    }
    files.toList
  }

  private def getConnection = {
    connection
  }

  private def getObjectPath(fileName: String): String = {
    if (path.isEmpty) {
      fileName
    } else {
      if (path.endsWith("/")) {
        "%s%s".format(path, fileName)
      } else {
        "%s/%s".format(path, fileName)
      }
    }
  }

  /** * Upload a text file to S3 (bytes are saved in UTF-8)
    *
    * @param fileName    Full path filename
    * @param fileContent String content of file
    * @return Boolean to determine success
    */
  def upload(fileName: String, fileContent: String): Unit = {
    val file = fileContent.getBytes("UTF-8")
    val metaData = new ObjectMetadata
    metaData.setContentLength(file.length.toLong)
    val contentStream = new ByteArrayInputStream(file)
    upload(fileName, contentStream, metaData)
  }

  private def upload(fileName: String, contentStream: ByteArrayInputStream, metaData: ObjectMetadata): Unit = {
    val objectPath = getObjectPath(fileName)
    getConnection.putObject(bucketName, objectPath, contentStream, metaData)
    contentStream.close()
  }

  /** * Upload a file to S3
    *
    * @param fileName    Full path filename
    * @param fileContent Byte content of file
    * @return Boolean to determine success
    */
  def upload(fileName: String, fileContent: Array[Byte]): Unit = {
    val metaData = new ObjectMetadata
    metaData.setContentLength(fileContent.length.toLong)
    val contentStream = new ByteArrayInputStream(fileContent)
    upload(fileName, contentStream, metaData)
  }

  def shutdown: Unit = {
    connection.shutdown
  }

}

object AmazonS3Client {
  def apply(): AmazonS3Client = {
    val accessKey = Environment.getProperty(AmazonS3Config.AccessKeyKey)
    val bucketName = Environment.getProperty(AmazonS3Config.BucketNameKey)
    val path = Environment.getProperty(AmazonS3Config.PathKey)
    val secret = Environment.getProperty(AmazonS3Config.SecretKey)
    val socketTimeout = Environment.getProperty(AmazonS3Config.SocketTimeoutKey).toInt
    new AmazonS3Client(accessKey, secret, socketTimeout, bucketName, path)
  }

  def apply(bucketName: String, path: String): AmazonS3Client = {
    val accessKey = Environment.getProperty(AmazonS3Config.AccessKeyKey)
    val secret = Environment.getProperty(AmazonS3Config.SecretKey)
    val socketTimeout = Environment.getProperty(AmazonS3Config.SocketTimeoutKey).toInt
    new AmazonS3Client(accessKey, secret, socketTimeout, bucketName, path)
  }

  def apply(path: String): AmazonS3Client = {
    val accessKey = Environment.getProperty(AmazonS3Config.AccessKeyKey)
    val bucketName = Environment.getProperty(AmazonS3Config.BucketNameKey)
    val secret = Environment.getProperty(AmazonS3Config.SecretKey)
    val socketTimeout = Environment.getProperty(AmazonS3Config.SocketTimeoutKey).toInt
    new AmazonS3Client(accessKey, secret, socketTimeout, bucketName, path)
  }
}
