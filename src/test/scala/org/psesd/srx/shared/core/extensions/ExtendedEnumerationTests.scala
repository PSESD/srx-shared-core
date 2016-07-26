package org.psesd.srx.shared.core.extensions

import org.psesd.srx.shared.core.sif.SifHeader
import org.scalatest.FunSuite

class ExtendedEnumerationTests extends FunSuite {

  test("withNameCaseInsensitive all lower") {
    val expected = SifHeader.Authorization
    val actual = SifHeader.withNameCaseInsensitive("authorization")
    assert(actual.equals(expected))
  }

  test("withNameCaseInsensitive all caps") {
    val expected = SifHeader.Authorization
    val actual = SifHeader.withNameCaseInsensitive("AUTHORIZATION")
    assert(actual.equals(expected))
  }

  test("withNameCaseInsensitive mixed case") {
    val expected = SifHeader.Authorization
    val actual = SifHeader.withNameCaseInsensitive("Authorization")
    assert(actual.equals(expected))
  }

}
