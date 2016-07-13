package org.psesd.srx.shared.core.sif

import org.scalatest.FunSuite

class SifRequestActionTests extends FunSuite {

  test("fromHttpMethod DELETE --> DELETE") {
    assert(SifRequestAction.fromHttpMethod(SifHttpRequestMethod.Delete).equals(SifRequestAction.Delete))
  }

  test("fromHttpMethod GET --> QUERY") {
    assert(SifRequestAction.fromHttpMethod(SifHttpRequestMethod.Get).equals(SifRequestAction.Query))
  }

  test("fromHttpMethod POST --> CREATE") {
    assert(SifRequestAction.fromHttpMethod(SifHttpRequestMethod.Post).equals(SifRequestAction.Create))
  }

  test("fromHttpMethod PUT --> UPDATE") {
    assert(SifRequestAction.fromHttpMethod(SifHttpRequestMethod.Put).equals(SifRequestAction.Update))
  }

}
