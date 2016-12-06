package org.psesd.srx.shared.core.config

import org.psesd.srx.shared.core.TestValues
import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyOrWhitespaceException, EnvironmentException}
import org.scalatest.FunSuite

class ZoneConfigTests extends FunSuite {

  test("constructor null zone") {
    val zoneId = null
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      new ZoneConfig(zoneId, TestValues.srxService.service.name)
    }
    assert(thrown.getMessage.equals("The zoneId cannot be null, empty, or whitespace."))
  }

  test("constructor null serviceName") {
    val zoneId = "foo"
    val serviceName = null
    val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
      new ZoneConfig(zoneId, serviceName)
    }
    assert(thrown.getMessage.equals("The serviceName cannot be null, empty, or whitespace."))
  }

  test("constructor invalid zone") {
    val zoneId = "foo"
    val thrown = intercept[EnvironmentException] {
      new ZoneConfig(zoneId, TestValues.srxService.service.name)
    }
    assert(thrown.getMessage.equals("XSRE configuration missing for zone 'foo'."))
  }

  test("constructor valid") {
    val zoneId = "test"
    val zoneConfig = new ZoneConfig(zoneId, TestValues.srxService.service.name)
    assert(zoneConfig.zoneId.equals(zoneId))
    assert(zoneConfig.cacheBucketName.equals("p2-xsre-cache-dev"))
    assert(zoneConfig.cachePath.equals("test"))
    assert(zoneConfig.schemaPath.equals("xsd/sif_3_3"))
    assert(zoneConfig.schemaRootFileName.equals("SIFNAxSRE.xsd"))
  }

}
