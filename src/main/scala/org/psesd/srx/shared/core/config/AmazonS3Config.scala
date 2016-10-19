package org.psesd.srx.shared.core.config

import scala.xml.NodeSeq

/** Amazon S3 configuration.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class AmazonS3Config(configXml: NodeSeq) {
  val accessKey = (configXml \ "accessKey").text
  val bucketName = (configXml \ "bucketName").text
  val path = (configXml \ "path").text
  val secret = (configXml \ "secret").text
  val socketTimeout = (configXml \ "socketTimeout").text.toInt
}

object AmazonS3Config {
  final val AccessKeyKey = "AMAZON_S3_ACCESS_KEY"
  final val BucketNameKey = "AMAZON_S3_BUCKET_NAME"
  final val PathKey = "AMAZON_S3_PATH"
  final val SecretKey = "AMAZON_S3_SECRET"
  final val SocketTimeoutKey = "AMAZON_S3_TIMEOUT"
}