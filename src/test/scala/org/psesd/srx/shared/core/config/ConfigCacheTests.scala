package org.psesd.srx.shared.core.config

import org.psesd.srx.shared.core.TestValues
import org.psesd.srx.shared.core.exceptions.EnvironmentException
import org.psesd.srx.shared.core.sif.SifRequestParameter
import org.scalatest.FunSuite

class ConfigCacheTests extends FunSuite {

  test("constructor invalid zone") {
    val zoneId = "foo"
    val thrown = intercept[EnvironmentException] {
      ConfigCache.getConfig(zoneId, TestValues.srxService.service.name)
    }
    assert(thrown.getMessage.equals("The requested ZoneConfig resource was not found."))
  }

  test("get zone config") {
    val zoneId = "test"
    val zoneConfig = ConfigCache.getConfig(zoneId, TestValues.srxService.service.name)
    assert(zoneConfig.zoneId.equals(zoneId))
  }

  test("clear cache") {
    val result = ConfigCache.delete(List[SifRequestParameter]())
    assert(result.statusCode == 200)
  }

}
