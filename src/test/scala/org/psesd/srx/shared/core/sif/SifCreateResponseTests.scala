package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.TypeExtensions._

import org.scalatest.FunSuite

class SifCreateResponseTests extends FunSuite {

  test("no results") {
    val actual = SifCreateResponse().toXml.toXmlString
    assert(actual.equals("<createResponse></createResponse>"))
  }

  test("one result") {
    val expectedLinux = "{\n  \"createResponse\" : {\n    \"creates\" : {\n      \"id\" : \"123\",\n      \"advisoryId\" : \"1\",\n      \"statusCode\" : \"1\"\n    }\n  }\n}"
    val expectedWindows = "{\r\n  \"createResponse\" : {\r\n    \"creates\" : {\r\n      \"id\" : \"123\",\r\n      \"advisoryId\" : \"1\",\r\n      \"statusCode\" : \"1\"\r\n    }\r\n  }\r\n}"
    val actual = SifCreateResponse().addResult("123", 1).toXml.toJsonString
    assert(actual.equals(expectedLinux) || actual.equals(expectedWindows))
  }

  test("two results") {
    val expectedLinux = "{\n  \"createResponse\" : {\n    \"creates\" : {\n      \"create\" : [ {\n        \"id\" : \"123\",\n        \"advisoryId\" : \"1\",\n        \"statusCode\" : \"1\"\n      }, {\n        \"id\" : \"456\",\n        \"advisoryId\" : \"2\",\n        \"statusCode\" : \"1\"\n      } ]\n    }\n  }\n}"
    val expectedWindows = "{\r\n  \"createResponse\" : {\r\n    \"creates\" : {\r\n      \"create\" : [ {\r\n        \"id\" : \"123\",\r\n        \"advisoryId\" : \"1\",\r\n        \"statusCode\" : \"1\"\r\n      }, {\r\n        \"id\" : \"456\",\r\n        \"advisoryId\" : \"2\",\r\n        \"statusCode\" : \"1\"\r\n      } ]\r\n    }\r\n  }\r\n}"
    val actual = SifCreateResponse().addResult("123", 1).addResult("456", 1).toXml.toJsonString
    assert(actual.equals(expectedLinux) || actual.equals(expectedWindows))
  }

}
