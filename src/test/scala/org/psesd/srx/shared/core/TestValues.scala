package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.sif._

object TestValues {

  lazy val sifAuthenticationMethod = SifAuthenticationMethod.SifHmacSha256
  lazy val sessionToken = SifProviderSessionToken("ad53dbf6-e0a0-469f-8428-c17738eba43e")
  lazy val sharedSecret = SifProviderSharedSecret("pHkAuxdGGMWS")
  lazy val sifUrl: SifProviderUrl = SifProviderUrl("http://localhost:%s".format(Environment.getPropertyOrElse("SERVER_PORT", "80")))
  lazy val sifProvider = new SifProvider(sifUrl, sessionToken, sharedSecret, sifAuthenticationMethod)
  lazy val timestamp: SifTimestamp = SifTimestamp("2015-02-24T20:51:59.878Z")
  lazy val authorization = new SifAuthorization(sifProvider, timestamp)

  val srxServiceBuildComponents = List[SrxServiceComponent](
    new SrxServiceComponent("jdk", "1.8"),
    new SrxServiceComponent("scala", "2.11.8"),
    new SrxServiceComponent("sbt", "0.13.12")
  )

  val srxService = new SrxService(new SrxServiceComponent("srx-shared-core", "1.0.1"), srxServiceBuildComponents)

}
