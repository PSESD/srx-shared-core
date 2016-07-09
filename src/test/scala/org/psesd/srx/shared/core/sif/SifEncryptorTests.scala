package org.psesd.srx.shared.core.sif

import java.security.InvalidAlgorithmParameterException
import javax.crypto.{Cipher, IllegalBlockSizeException}

import org.apache.commons.codec.binary.Base64
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class SifEncryptorTests extends FunSuite {

  val password: Array[Char] = "Pass@word1".toCharArray
  val salt: Array[Byte] = "S@1tS@1t".getBytes
  val iv: Array[Byte] = "e675f725e675f725".getBytes
  val shortIv: Array[Byte] = "e675f725e675".getBytes
  val longIv: Array[Byte] = "e675f725e675f725e675".getBytes
  val algorithm: String = "AES/CBC/PKCS5Padding"
  val aesAlgorithm: String = "AES"
  val aesProvider: String = "SunJCE version 1.8"
  val blockSize: Int = 16
  val plaintext: String = "Hello World"
  val encrypted: String = "PlzzaSQM/evjTeBbdmvoBg=="

  Map("encrypt" -> Cipher.ENCRYPT_MODE,
    "decrypt" -> Cipher.DECRYPT_MODE).foreach { case (key, value) =>

    test(s"get $key cipher") {
      val result: Cipher = SifEncryptor.getCipher(password, salt, iv, value)
      assert(result !== null)
      assert(result.getAlgorithm === algorithm)
      assert(result.getBlockSize === blockSize)
      assert(result.getExemptionMechanism === null)
      assert(result.getIV === iv)
      assert(result.getParameters.getAlgorithm === aesAlgorithm)
      assert(result.getProvider.toString === aesProvider)
    }
  }

  test("encrypt bytes") {
    val bytes: Array[Byte] = SifEncryptor.encrypt(password, salt, plaintext, iv)
    val result: String = Base64.encodeBase64String(bytes)
    assert(result === encrypted)
  }

  test("encrypt string") {
    val result: String = SifEncryptor.encryptString(password, salt, plaintext, iv)
    assert(result === encrypted)
  }

  test("decrypt bytes") {
    val bytes: Array[Byte] = SifEncryptor.decrypt(password, salt, encrypted, iv)
    val result: String = new String(bytes)
    assert(result === plaintext)
  }

  test("decrypt string") {
    val result: String = SifEncryptor.decryptString(password, salt, encrypted, iv)
    assert(result === plaintext)
  }

  Map("null" -> null,
    "empty" -> new Array[Byte](0)).foreach { case (key, value) =>

    test(s"get cipher with $key iv causes ArgumentNullOrEmptyException") {
      val thrown = intercept[ArgumentNullOrEmptyException] {
        SifEncryptor.getCipher(password, salt, value, Cipher.ENCRYPT_MODE)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("iv parameter")))
    }

    test(s"encrypt with $key iv causes ArgumentNullOrEmptyException") {
      val thrown = intercept[ArgumentNullOrEmptyException] {
        SifEncryptor.encrypt(password, salt, plaintext, value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("iv parameter")))
    }

    test(s"decrypt with $key iv causes ArgumentNullOrEmptyException") {
      val thrown = intercept[ArgumentNullOrEmptyException] {
        SifEncryptor.decrypt(password, salt, encrypted, value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("iv parameter")))
    }

  }

  Map("short iv" -> shortIv,
    "long iv" -> longIv).foreach { case (key, value) =>

    test(s"get cipher with $key causes InvalidAlgorithmParameterException") {
      val thrown = intercept[InvalidAlgorithmParameterException] {
        SifEncryptor.getCipher(password, salt, value, Cipher.ENCRYPT_MODE)
      }
      assert(thrown.getMessage.equals("Wrong IV length: must be 16 bytes long"))
    }

    test(s"encrypt with $key causes InvalidAlgorithmParameterException") {
      val thrown = intercept[InvalidAlgorithmParameterException] {
        SifEncryptor.encrypt(password, salt, plaintext, value)
      }
      assert(thrown.getMessage.equals("Wrong IV length: must be 16 bytes long"))
    }

    test(s"decrypt with $key causes InvalidAlgorithmParameterException") {
      val thrown = intercept[InvalidAlgorithmParameterException] {
        SifEncryptor.decrypt(password, salt, encrypted, value)
      }
      assert(thrown.getMessage.equals("Wrong IV length: must be 16 bytes long"))
    }

  }

  test("get cipher with invalid mode causes ArgumentInvalidException") {
    val thrown = intercept[ArgumentInvalidException] {
      val unused = SifEncryptor.getCipher(password, salt, iv, 9)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("mode parameter")))
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"encrypt $key value causes ArgumentNullOrEmptyOrWhitespaceException") {

      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        SifEncryptor.encrypt(password, salt, value, iv)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("value parameter")))
    }

    test(s"decrypt $key value causes ArgumentNullOrEmptyOrWhitespaceException") {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        SifEncryptor.decrypt(password, salt, value, iv)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("value parameter")))
    }

  }

  Map("unencrypted" -> "Hello World",
    "non-Base64" -> "A:B:C:D").foreach { case (key, value) =>

    test(s"decrypt $key value causes ArgumentInvalidException") {
      val thrown = intercept[ArgumentInvalidException] {
        SifEncryptor.decrypt(password, salt, value, iv)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("value parameter")))
    }

  }

  test("decrypting valid Base64 unencrypted value causes IllegalBlockSizeException") {
    val thrown = intercept[IllegalBlockSizeException] {
      SifEncryptor.decrypt(password, salt, "HelloWorld", iv)
    }
    assert(thrown.getMessage.equals("Input length must be multiple of 16 when decrypting with padded cipher"))
  }
}
