package org.psesd.srx.shared.core.sif

import java.security.InvalidAlgorithmParameterException
import javax.crypto.{Cipher, IllegalBlockSizeException}

import org.apache.commons.codec.binary.Base64
import org.psesd.srx.shared.core.exceptions._
import org.scalatest.FunSuite

class SifEncryptorTests extends FunSuite {

  Map("encrypt" -> Cipher.ENCRYPT_MODE,
    "decrypt" -> Cipher.DECRYPT_MODE).foreach { case (key, value) =>

    test(s"get $key cipher") {
      val result: Cipher = SifEncryptor.getCipher(SifTestValues.password, SifTestValues.salt, SifTestValues.iv, value)
      assert(result !== null)
      assert(result.getAlgorithm === SifTestValues.algorithm)
      assert(result.getBlockSize === SifTestValues.blockSize)
      assert(result.getExemptionMechanism === null)
      assert(result.getIV === SifTestValues.iv)
      assert(result.getParameters.getAlgorithm === SifTestValues.aesAlgorithm)
      assert(result.getProvider.toString === SifTestValues.aesProvider)
    }
  }

  test("encrypt bytes") {
    val bytes: Array[Byte] = SifEncryptor.encrypt(SifTestValues.password, SifTestValues.salt, SifTestValues.plaintext, SifTestValues.iv)
    val result: String = Base64.encodeBase64String(bytes)
    assert(result === SifTestValues.encrypted)
  }

  test("encrypt string") {
    val result: String = SifEncryptor.encryptString(SifTestValues.password, SifTestValues.salt, SifTestValues.plaintext, SifTestValues.iv)
    assert(result === SifTestValues.encrypted)
  }

  test("decrypt bytes") {
    val bytes: Array[Byte] = SifEncryptor.decrypt(SifTestValues.password, SifTestValues.salt, SifTestValues.encrypted, SifTestValues.iv)
    val result: String = new String(bytes)
    assert(result === SifTestValues.plaintext)
  }

  test("decrypt string") {
    val result: String = SifEncryptor.decryptString(SifTestValues.password, SifTestValues.salt, SifTestValues.encrypted, SifTestValues.iv)
    assert(result === SifTestValues.plaintext)
  }

  Map("null" -> null,
    "empty" -> new Array[Byte](0)).foreach { case (key, value) =>

    test(s"get cipher with $key iv causes ArgumentNullOrEmptyException") {
      val thrown = intercept[ArgumentNullOrEmptyException] {
        SifEncryptor.getCipher(SifTestValues.password, SifTestValues.salt, value, Cipher.ENCRYPT_MODE)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("iv parameter")))
    }

    test(s"encrypt with $key iv causes ArgumentNullOrEmptyException") {
      val thrown = intercept[ArgumentNullOrEmptyException] {
        SifEncryptor.encrypt(SifTestValues.password, SifTestValues.salt, SifTestValues.plaintext, value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("iv parameter")))
    }

    test(s"decrypt with $key iv causes ArgumentNullOrEmptyException") {
      val thrown = intercept[ArgumentNullOrEmptyException] {
        SifEncryptor.decrypt(SifTestValues.password, SifTestValues.salt, SifTestValues.encrypted, value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("iv parameter")))
    }

  }

  Map("short iv" -> SifTestValues.shortIv,
    "long iv" -> SifTestValues.longIv).foreach { case (key, value) =>

    test(s"get cipher with $key causes InvalidAlgorithmParameterException") {
      val thrown = intercept[InvalidAlgorithmParameterException] {
        SifEncryptor.getCipher(SifTestValues.password, SifTestValues.salt, value, Cipher.ENCRYPT_MODE)
      }
      assert(thrown.getMessage.equals("Wrong IV length: must be 16 bytes long"))
    }

    test(s"encrypt with $key causes InvalidAlgorithmParameterException") {
      val thrown = intercept[InvalidAlgorithmParameterException] {
        SifEncryptor.encrypt(SifTestValues.password, SifTestValues.salt, SifTestValues.plaintext, value)
      }
      assert(thrown.getMessage.equals("Wrong IV length: must be 16 bytes long"))
    }

    test(s"decrypt with $key causes InvalidAlgorithmParameterException") {
      val thrown = intercept[InvalidAlgorithmParameterException] {
        SifEncryptor.decrypt(SifTestValues.password, SifTestValues.salt, SifTestValues.encrypted, value)
      }
      assert(thrown.getMessage.equals("Wrong IV length: must be 16 bytes long"))
    }

  }

  test("get cipher with invalid mode causes ArgumentInvalidException") {
    val thrown = intercept[ArgumentInvalidException] {
      val unused = SifEncryptor.getCipher(SifTestValues.password, SifTestValues.salt, SifTestValues.iv, 9)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("mode parameter")))
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"encrypt $key value causes ArgumentNullOrEmptyOrWhitespaceException") {

      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        SifEncryptor.encrypt(SifTestValues.password, SifTestValues.salt, value, SifTestValues.iv)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("value parameter")))
    }

    test(s"decrypt $key value causes ArgumentNullOrEmptyOrWhitespaceException") {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        SifEncryptor.decrypt(SifTestValues.password, SifTestValues.salt, value, SifTestValues.iv)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("value parameter")))
    }

  }

  Map("unencrypted" -> "Hello World",
    "non-Base64" -> "A:B:C:D").foreach { case (key, value) =>

    test(s"decrypt $key value causes ArgumentInvalidException") {
      val thrown = intercept[ArgumentInvalidException] {
        SifEncryptor.decrypt(SifTestValues.password, SifTestValues.salt, value, SifTestValues.iv)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.IsInvalid.format("value parameter")))
    }

  }

  test("decrypting valid Base64 unencrypted value causes IllegalBlockSizeException") {
    val thrown = intercept[IllegalBlockSizeException] {
      SifEncryptor.decrypt(SifTestValues.password, SifTestValues.salt, "HelloWorld", SifTestValues.iv)
    }
    assert(thrown.getMessage.equals("Input length must be multiple of 16 when decrypting with padded cipher"))
  }

  test("get SIF hash") {
    val result: String = SifEncryptor.getSifAuthorizationHmacSha256Hash(SifTestValues.sifProvider, SifTestValues.timestamp)
    assert(result === SifTestValues.sifAuthorizationHash)
  }

  test("get SIF hash with null provider causes ArgumentNullException") {
    val thrown = intercept[ArgumentNullException] {
      SifEncryptor.getSifAuthorizationHmacSha256Hash(null, SifTestValues.timestamp)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("provider parameter")))
  }

  test("get SIF hash with null timestamp causes ArgumentNullException") {
    val thrown = intercept[ArgumentNullException] {
      SifEncryptor.getSifAuthorizationHmacSha256Hash(SifTestValues.sifProvider, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("timestamp parameter")))
  }

}
