package org.psesd.srx.shared.core.io

import com.amazonaws.services.s3.model.AmazonS3Exception
import org.psesd.srx.shared.core.config.{AmazonS3Config, Environment}
import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class AmazonS3ClientTests extends FunSuite {
  val environment = Environment.name
  val accessKey = Environment.getProperty(AmazonS3Config.AccessKeyKey)
  val bucketName = Environment.getProperty(AmazonS3Config.BucketNameKey)
  val path = Environment.getProperty(AmazonS3Config.PathKey)
  val secret = Environment.getProperty(AmazonS3Config.SecretKey)
  val socketTimeout = Environment.getProperty(AmazonS3Config.SocketTimeoutKey).toInt
  val testClient = new AmazonS3Client(accessKey, secret, socketTimeout, bucketName, path)
  val invalidClient = new AmazonS3Client(new AmazonS3Config(
    <amazonS3>
      <accessKey>invalidkey</accessKey>
      <secret>invalidsecret</secret>
      <socketTimeout>300000</socketTimeout>
      <bucketName>invalidbucket</bucketName>
      <path>invalidpath</path>
    </amazonS3>))

  test("accessKey null") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val client = new AmazonS3Client(null, "invalidSecret", 0, "invalidBucket", "invalidPath")
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("accessKey")
    assert(thrown.getMessage.equals(expected))
  }

  test("secret null") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val client = new AmazonS3Client("invalidAccessKey", null, 0, "invalidBucket", "invalidPath")
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("secret")
    assert(thrown.getMessage.equals(expected))
  }

  test("bucketName null") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val client = new AmazonS3Client("invalidAccessKey", "invalidSecret", 0, null, "invalidPath")
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("bucketName")
    assert(thrown.getMessage.equals(expected))
  }

  test("path null") {
    val thrown = intercept[ArgumentNullException] {
      val client = new AmazonS3Client("invalidAccessKey", "invalidSecret", 0, "invalidBucket", null)
    }
    val expected = ExceptionMessage.NotNull.format("path")
    assert(thrown.getMessage.equals(expected))
  }

  test("default factory") {
    if (Environment.isLocal) {
      val client = AmazonS3Client()
      assert(!client.path.isEmpty)
      client.shutdown
    }
  }

  test("path factory") {
    if (Environment.isLocal) {
      val client = AmazonS3Client("testPath")
      assert(!client.path.isEmpty)
      client.shutdown
    }
  }

  test("bucketName and path factory") {
    if (Environment.isLocal) {
      val client = AmazonS3Client("testBucket", "testPath")
      assert(!client.path.isEmpty)
      client.shutdown
    }
  }

  test("can delete") {
    if (Environment.isLocal) {
      val testFileName = "test.xml"
      val testFileContent = "<xml>test</xml>"
      testClient.upload(testFileName, testFileContent)

      testClient.delete(testFileName)
    }
  }

  test("can delete file that does not exist") {
    if (Environment.isLocal) {
      testClient.delete("not_a_file")
    }
  }

  test("cannot delete with invalid credentials") {
    if (Environment.isLocal) {
      val thrown = intercept[AmazonS3Exception] {
        invalidClient.delete("not_a_file")
      }
      val expected = "InvalidAccessKeyId"
      assert(thrown.getErrorCode.equals(expected))
    }
  }

  test("can download") {
    if (Environment.isLocal) {
      val testFileName = "test.xml"
      val testFileContent = "<xml>test</xml>"
      testClient.upload(testFileName, testFileContent)

      val expected = "<xml>test</xml>"
      val result = testClient.download(testFileName)
      assert(result.equals(expected))

      testClient.delete(testFileName)
    }
  }

  test("cannot download file that does not exist") {
    if (Environment.isLocal) {
      val thrown = intercept[AmazonS3Exception] {
        val result = testClient.download("not_a_file")
      }
      val expected = "NoSuchKey"
      assert(thrown.getErrorCode.equals(expected))
    }
  }

  test("cannot download file with invalid credentials") {
    if (Environment.isLocal) {
      val thrown = intercept[AmazonS3Exception] {
        val result = invalidClient.download("not_a_file")
      }
      val expected = "InvalidAccessKeyId"
      assert(thrown.getErrorCode.equals(expected))
    }
  }

  test("file exists") {
    if (Environment.isLocal) {
      val testFileName = "test.xml"
      val testFileContent = "<xml>test</xml>"
      testClient.upload(testFileName, testFileContent)

      val fileExists = testClient.fileExists(testFileName)
      assert(fileExists)

      testClient.delete(testFileName)
    }
  }

  test("file does not exist") {
    if (Environment.isLocal) {
      val fileExists = testClient.fileExists("not_a_file")
      assert(!fileExists)
    }
  }

  test("file does not exist with invalid credentials") {
    if (Environment.isLocal) {
      val thrown = intercept[AmazonS3Exception] {
        val result = invalidClient.fileExists("not_a_file")
      }
      val expected = "403 Forbidden"
      assert(thrown.getErrorCode.equals(expected))
    }
  }

  test("can upload new") {
    if (Environment.isLocal) {
      val testFileName = "test.xml"
      val testFileContent = "<xml>test</xml>"

      testClient.upload(testFileName, testFileContent)

      val fileExists = testClient.fileExists(testFileName)
      assert(fileExists)

      testClient.delete(testFileName)
    }
  }

  test("can upload overwrite") {
    if (Environment.isLocal) {
      val testFileName = "test.xml"
      val testFileContentOriginal = "<xml>test ORIGINAL</xml>"
      testClient.upload(testFileName, testFileContentOriginal)

      val testFileContentNew = "<xml>test UPDATED</xml>"
      testClient.upload(testFileName, testFileContentNew)

      val expected = "<xml>test UPDATED</xml>"
      val result = testClient.download(testFileName)
      assert(result.equals(expected))

      testClient.delete(testFileName)
    }
  }

  test("can upload bytes") {
    if (Environment.isLocal) {
      val testFileName = "test.xml"
      val testFileContent = "<xml>test</xml>"
      val testFileBytes = testFileContent.getBytes("UTF-8")

      testClient.upload(testFileName, testFileBytes)

      val fileExists = testClient.fileExists(testFileName)
      assert(fileExists)

      testClient.delete(testFileName)
    }
  }

  test("can shutdown") {
    testClient.shutdown
  }

}
