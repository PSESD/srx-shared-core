package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.sif._

import scala.xml.Node

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

  val srxService = new SrxService(new SrxServiceComponent("srx-shared-core-test", "1.0.1"), srxServiceBuildComponents)

  class TestEntity(val id: String) extends SrxResource

  object TestEntity {
    def apply(node: Node): TestEntity = new TestEntity("123")
  }

  class TestEntityResult(val id: String) extends SrxResourceResult {
    def toXml: Option[Node] = Some(<test id={id}/>)
  }

  object TestEntityService extends SrxResourceService {
    def delete(srxResource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult = {
      throw new NotImplementedError("DELETE not implemented.")
    }
    def create(srxResource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult = {
      val testEntity = srxResource.asInstanceOf[TestEntity]
      new TestEntityResult(testEntity.id)
    }
    def query(srxResource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult = {
      throw new NotImplementedError("QUERY not implemented.")
    }
    def update(srxResource: SrxResource, parameters: List[SifRequestParameter]): SrxResourceResult = {
      throw new NotImplementedError("UPDATE not implemented.")
    }
  }

}
