package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyException, ExceptionMessage}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.scalatest.FunSuite

class SrxServiceTests extends FunSuite {

  test("valid service") {
    val service = new SrxService(TestValues.srxService.service, TestValues.srxService.buildComponents)
    assert(service.service.name.equals(TestValues.srxService.service.name))
    assert(service.service.version.equals(TestValues.srxService.service.version))
    assert(service.buildComponents.head.name.equals(TestValues.srxService.buildComponents.head.name))
    assert(service.buildComponents.head.version.equals(TestValues.srxService.buildComponents.head.version))
  }

  test("null service") {

    val thrown = intercept[ArgumentNullException] {
      new SrxService(null, TestValues.srxService.buildComponents)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("service parameter")))
  }

  test("null build components") {

    val thrown = intercept[ArgumentNullOrEmptyException] {
      new SrxService(TestValues.srxService.service, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("buildComponents parameter")))
  }

  test("empty build components") {

    val thrown = intercept[ArgumentNullOrEmptyException] {
      new SrxService(TestValues.srxService.service, List[SrxServiceComponent]())
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmpty.format("buildComponents parameter")))
  }

  test("toXml") {
    val service = TestValues.srxService
    val serviceXml = service.toXml.toXmlString
    assert(!serviceXml.isNullOrEmpty)
  }

}
