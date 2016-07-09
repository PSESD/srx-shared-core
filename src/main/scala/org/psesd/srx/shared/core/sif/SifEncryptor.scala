package org.psesd.srx.shared.core.sif

import javax.crypto.spec.{IvParameterSpec, PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, SecretKeyFactory}

import org.apache.commons.codec.binary.Base64
import org.psesd.srx.shared.core.exceptions.{ArgumentInvalidException, ArgumentNullOrEmptyException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Provides SIF cryptography methods.
  *
  * @version 1.0
  * @since 1.0
  * @author David S. Dennison (iTrellis, LLC)
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object SifEncryptor {

  def encryptString(password: Array[Char], salt: Array[Byte], value: String, iv: Array[Byte]): String = {
    val encrypted: Array[Byte] = encrypt(password, salt, value, iv)
    Base64.encodeBase64String(encrypted)
  }

  def encrypt(password: Array[Char], salt: Array[Byte], value: String, iv: Array[Byte]): Array[Byte] = {
    if (password.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("password parameter")
    }

    if (salt.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("salt parameter")
    }

    if (value.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("value parameter")
    }

    val cipher = getCipher(password, salt, iv, Cipher.ENCRYPT_MODE)
    cipher.doFinal(value.trim.getBytes)
  }

  def decryptString(password: Array[Char], salt: Array[Byte], value: String, iv: Array[Byte]): String = {
    val decryption: Array[Byte] = decrypt(password, salt, value, iv)
    new String(decryption)
  }

  def decrypt(password: Array[Char], salt: Array[Byte], value: String, iv: Array[Byte]): Array[Byte] = {
    if (password.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("password parameter")
    }

    if (salt.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("salt parameter")
    }

    if (value.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("value parameter")
    }

    if (!Base64.isBase64(value) || value.trim.contains(" ")) {
      throw new ArgumentInvalidException("value parameter")
    }

    if (iv.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("iv parameter")
    }

    val cipher = getCipher(password, salt, iv, Cipher.DECRYPT_MODE)
    cipher.doFinal(Base64.decodeBase64(value.trim.getBytes))
  }

  def getCipher(password: Array[Char], salt: Array[Byte], iv: Array[Byte], mode: Int): Cipher = {

    if (password.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("password parameter")
    }

    if (salt.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("salt parameter")
    }

    if (iv.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyException("iv parameter")
    }

    if (mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE) {
      throw new ArgumentInvalidException("mode parameter")
    }

    val iteration = 65536
    val keyLength = 128

    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val spec = new PBEKeySpec(password, salt, iteration, keyLength)
    val key = new SecretKeySpec(factory.generateSecret(spec).getEncoded, "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    cipher.init(mode, key, new IvParameterSpec(iv))
    cipher
  }

}
