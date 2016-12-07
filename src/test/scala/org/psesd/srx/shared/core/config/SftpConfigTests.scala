package org.psesd.srx.shared.core.config

import org.psesd.srx.shared.core.exceptions._
import org.scalatest.FunSuite

class SftpConfigTests extends FunSuite {

  val testConfig = <sftp>
    <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
    <folder>testsd</folder>
    <privateKeyPath>psesdtest</privateKeyPath>
    <publicKeyPath>psesdtest.pub</publicKeyPath>
  </sftp>

  test("configXml null") {
    val thrown = intercept[ArgumentNullException] {
      val config = new SftpConfig(null)
    }
    val expected = ExceptionMessage.NotNull.format("configXml")
    assert(thrown.getMessage === expected)
  }

  test("folder empty") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
          <folder></folder>
          <privateKeyPath>psesdtest</privateKeyPath>
          <publicKeyPath>psesdtest.pub</publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("folder")
    assert(thrown.getMessage === expected)
  }

  test("folder whitespace") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
          <folder></folder>
          <privateKeyPath>psesdtest</privateKeyPath>
          <publicKeyPath>psesdtest.pub</publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("folder")
    assert(thrown.getMessage === expected)
  }

  test("privateKeyPath empty") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
          <folder>testsd</folder>
          <privateKeyPath></privateKeyPath>
          <publicKeyPath>psesdtest.pub</publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("privateKeyPath")
    assert(thrown.getMessage === expected)
  }

  test("privateKeyPath whitespace") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
          <folder>testsd</folder>
          <privateKeyPath></privateKeyPath>
          <publicKeyPath>psesdtest.pub</publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("privateKeyPath")
    assert(thrown.getMessage === expected)
  }

  test("publicKeyPath empty") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
          <folder>testsd</folder>
          <privateKeyPath>psesdtest</privateKeyPath>
          <publicKeyPath></publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("publicKeyPath")
    assert(thrown.getMessage === expected)
  }

  test("publicKeyPath whitespace") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
          <folder>testsd</folder>
          <privateKeyPath>psesdtest</privateKeyPath>
          <publicKeyPath></publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("publicKeyPath")
    assert(thrown.getMessage === expected)
  }

  test("uri empty") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri></uri>
          <folder>testsd</folder>
          <privateKeyPath>psesdtest</privateKeyPath>
          <publicKeyPath>psesdtest.pub</publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("uri")
    assert(thrown.getMessage === expected)
  }

  test("uri whitespace") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri></uri>
          <folder>testsd</folder>
          <privateKeyPath>psesdtest</privateKeyPath>
          <publicKeyPath>psesdtest.pub</publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("uri")
    assert(thrown.getMessage === expected)
  }

  test("uri invalid") {
    val thrown = intercept[ArgumentInvalidException] {
      val config = new SftpConfig(
        <sftp>
          <uri>invalid</uri>
          <folder>testsd</folder>
          <privateKeyPath>psesdtest</privateKeyPath>
          <publicKeyPath>psesdtest.pub</publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.IsInvalid.format("uri")
    assert(thrown.getMessage === expected)
  }

  test("uri host invalid") {
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      val config = new SftpConfig(
        <sftp>
          <uri>sftp://SRE:ThisIsMyPassphrase/misc/local</uri>
          <folder>testsd</folder>
          <privateKeyPath>psesdtest</privateKeyPath>
          <publicKeyPath>psesdtest.pub</publicKeyPath>
        </sftp>
      )
    }
    val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("uri host")
    assert(thrown.getMessage === expected)
  }

  test("privateKey invalid") {
    if(Environment.isLocalOrDevelopment) {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        val config = new SftpConfig(
          <sftp>
            <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
            <folder>testsd</folder>
            <privateKeyPath>notapath</privateKeyPath>
            <publicKeyPath>psesdtest.pub</publicKeyPath>
          </sftp>
        )
      }
      val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("privateKey")
      assert(thrown.getMessage === expected)
    }
  }

  test("publicKey invalid") {
    if(Environment.isLocalOrDevelopment) {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        val config = new SftpConfig(
          <sftp>
            <uri>sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local</uri>
            <folder>testsd</folder>
            <privateKeyPath>psesdtest</privateKeyPath>
            <publicKeyPath>notapath.pub</publicKeyPath>
          </sftp>
        )
      }
      val expected = ExceptionMessage.NotNullOrEmptyOrWhitespace.format("publicKey")
      assert(thrown.getMessage === expected)
    }
  }

  test("config valid") {
    if(Environment.isLocalOrDevelopment) {
      val config = new SftpConfig(testConfig)
      assert(config.folder.equals("testsd"))
      assert(config.privateKeyPath.equals("psesdtest"))
      assert(config.publicKeyPath.equals("psesdtest.pub"))
      assert(config.uriString.equals("sftp://SRE:ThisIsMyPassphrase@notadomain.net/misc/local"))
      assert(config.protocol.equals("sftp://"))
      assert(config.host.equals("notadomain.net"))
      assert(config.path.equals("/misc/local"))
      assert(config.user.equals("SRE"))
      assert(config.password.equals("ThisIsMyPassphrase"))
      assert(config.url.equals("misc/local"))
      assert(!config.getPrivateKey.isEmpty)
      assert(!config.getPublicKey.isEmpty)
    }
  }

}
