package org.psesd.srx.shared.core.extensions

import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.scalatest.FunSuite

class TypeExtensionTests extends FunSuite {

  test("array isNullOrEmpty null") {
    val array: Array[String] = null
    assert(array.isNullOrEmpty)
  }

  test("array isNullOrEmpty empty") {
    val array = Array[String]()
    assert(array.isNullOrEmpty)
  }

  test("array isNullOrEmpty false") {
    val array = Array[String]("test")
    assert(!array.isNullOrEmpty)
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ",
    "invalid" -> "abc").foreach { case (key, value) =>

    test(s"string isUuid $key") {
      val string: String = value
      assert(!string.isUuid)
    }
  }

  test("string isUuid true") {
    val string = "95e0f65e-c489-4cba-bbd0-4ea15b0a3e3c"
    assert(string.isUuid)
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"string isNullOrEmpty $key") {
      val string: String = value
      assert(string.isNullOrEmpty)
    }
  }

  test("string isNullOrEmpty false") {
    val string: String = "abc"
    assert(!string.isNullOrEmpty)
  }

  test("string trimPrecedingSlash") {
    assert("/test".trimPrecedingSlash.equals("test"))
    assert("test".trimPrecedingSlash.equals("test"))
    val string: String = null
    assert(string.trimPrecedingSlash == null)
  }

  test("string trimTrailingSlash") {
    assert("test/".trimTrailingSlash.equals("test"))
    assert("test".trimTrailingSlash.equals("test"))
    val string: String = null
    assert(string.trimTrailingSlash == null)
  }

  test("xml toJsonString") {
    val actual = <test><foo>fighter</foo><stack>overflow</stack></test>.toJsonString
    val expected = "{\r\n  \"test\" : {\r\n    \"foo\" : \"fighter\",\r\n    \"stack\" : \"overflow\"\r\n  }\r\n}"
    assert(actual.equals(expected))
  }

  test("xml toXmlString") {
    val actual = <test><foo>fighter</foo><stack>overflow</stack></test>.toXmlString
    val expected = "<test>\n  <foo>fighter</foo>\n  <stack>overflow</stack>\n</test>"
    assert(actual.equals(expected))
  }

}
