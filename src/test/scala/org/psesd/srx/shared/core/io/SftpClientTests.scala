package org.psesd.srx.shared.core.io

import org.psesd.srx.shared.core.config.SftpConfig
import org.psesd.srx.shared.core.exceptions.{ExceptionMessage, ArgumentNullException}
import org.scalatest.FunSuite

class SftpClientTests extends FunSuite {

  test("sftpConfig null") {
    val thrown = intercept[ArgumentNullException] {
      val client = new SftpClient(null)
    }
    val expected = ExceptionMessage.NotNull.format("sftpConfig")
    assert(thrown.getMessage === expected)
  }

  ignore("write to wa-k12.net") {
    val testConfig = new SftpConfig(
      <sftp>
        <uri>sftp://REDACTED</uri>
        <folder>testsd</folder>
        <privateKeyPath>psesdtest</privateKeyPath>
        <publicKeyPath>psesdtest.pub</publicKeyPath>
      </sftp>
    )
    val client = new SftpClient(testConfig)
    assert(client.write("test.xml", "<xml/>".getBytes))
    assert(client.exists("test.xml"))
  }

}
