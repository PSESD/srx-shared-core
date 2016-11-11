package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.sif.SifRequestParameter
import org.scalatest.FunSuite

class SrxResponseFormatTests extends FunSuite {

  test("get null parameters") {
    val format = SrxResponseFormat.getResponseFormat(null)
    assert(format.equals(SrxResponseFormat.Sif))
  }

  test("get empty parameters") {
    val format = SrxResponseFormat.getResponseFormat(List[SifRequestParameter]())
    assert(format.equals(SrxResponseFormat.Sif))
  }

  test("get invalid") {
    val format = SrxResponseFormat.getResponseFormat(List[SifRequestParameter](SifRequestParameter("ResponseFormat", "invalid")))
    assert(format.equals(SrxResponseFormat.Sif))
  }

  test("get sif") {
    val format = SrxResponseFormat.getResponseFormat(List[SifRequestParameter](SifRequestParameter("ResponseFormat", "SIF")))
    assert(format.equals(SrxResponseFormat.Sif))
  }

  test("get object") {
    val format = SrxResponseFormat.getResponseFormat(List[SifRequestParameter](SifRequestParameter("ResponseFormat", "Object")))
    assert(format.equals(SrxResponseFormat.Object))
  }

}
