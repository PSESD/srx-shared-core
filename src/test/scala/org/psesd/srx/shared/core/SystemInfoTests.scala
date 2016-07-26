package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.scalatest.FunSuite

class SystemInfoTests extends FunSuite {

  test("toXml") {
    val info = SystemInfo.toXml.toXmlString
    assert(!info.isNullOrEmpty)
  }

}
