package org.psesd.srx.shared.core

object TestValues {

  val srxServiceBuildComponents = List[SrxServiceComponent](
    new SrxServiceComponent("jdk", "1.8"),
    new SrxServiceComponent("scala", "2.11.8"),
    new SrxServiceComponent("sbt", "0.13.12")
  )

  val srxService = new SrxService(new SrxServiceComponent("srx-shard-core-test", "1.0.1"), srxServiceBuildComponents)

}
