package org.psesd.srx.shared.core

import org.scalatest.FunSuite

class SrxRequestTests extends FunSuite {

  test("empty request") {
    val request = new SrxRequest(0, null, null, null)
    assert(request.body == "")
  }

}
