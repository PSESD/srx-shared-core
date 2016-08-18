package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.TestValues
import org.scalatest.FunSuite

class SrxResourceServiceTests extends FunSuite {

  test("getIdFromRequestParameters") {
    val id = TestValues.TestEntityService.getIdFromRequestParameters(List[SifRequestParameter](SifRequestParameter("Id","123")))
    assert(id.get.equals("123"))
  }

}
