package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.sif.SifAuthenticationMethod.SifAuthenticationMethod

object SifTestValues {
  val aesAlgorithm: String = "AES"
  val aesProvider: String = "SunJCE version 1.8"
  val algorithm: String = "AES/CBC/PKCS5Padding"
  val blockSize: Int = 16
  val encrypted: String = "PlzzaSQM/evjTeBbdmvoBg=="
  val iv: Array[Byte] = "e675f725e675f725".getBytes
  val longIv: Array[Byte] = "e675f725e675f725e675".getBytes
  val password: Array[Char] = "Pass@word1".toCharArray
  val plaintext: String = "Hello World"
  val salt: Array[Byte] = "S@1tS@1t".getBytes
  val shortIv: Array[Byte] = "e675f725e675".getBytes
  val sifAuthenticationMethod = SifAuthenticationMethod.SifHmacSha256
  val sifAuthenticationMethods = List[SifAuthenticationMethod](sifAuthenticationMethod)
  val sifAuthorizationBasic = "Basic YWQ1M2RiZjYtZTBhMC00NjlmLTg0MjgtYzE3NzM4ZWJhNDNlOllXUTFNMlJpWmpZdFpUQmhNQzAwTmpsbUxUZzBNamd0WXpFM056TTRaV0poTkRObE9qSXdNVFV0TURJdE1qUlVNakE2TlRFNk5Ua3VPRGM0V2c9PQ=="
  val sifAuthorizationShaHmac256 = "SIF_HMACSHA256 YWQ1M2RiZjYtZTBhMC00NjlmLTg0MjgtYzE3NzM4ZWJhNDNlOmpVSnprUWhBWDBaSHB3a0VPSmMzQnE2dENjSjB2VUd3RGRMRndVdHFPSjA9"
  val sifAuthorizationHash: String = "jUJzkQhAX0ZHpwkEOJc3Bq6tCcJ0vUGwDdLFwUtqOJ0="

  lazy val sessionToken = SifProviderSessionToken("ad53dbf6-e0a0-469f-8428-c17738eba43e")
  lazy val sharedSecret = SifProviderSharedSecret("pHkAuxdGGMWS")
  lazy val sifUrl: SifProviderUrl = SifProviderUrl("https://psesd.hostedzone.com/svcs/dev/requestProvider")
  lazy val sifProvider = new SifProvider(sifUrl, sessionToken, sharedSecret, sifAuthenticationMethod)
  lazy val sifProviders = List[SifProvider](sifProvider)
  lazy val timestamp: SifTimestamp = SifTimestamp("2015-02-24T20:51:59.878Z")
  lazy val authorization = new SifAuthorization(sifProvider, timestamp)
}
