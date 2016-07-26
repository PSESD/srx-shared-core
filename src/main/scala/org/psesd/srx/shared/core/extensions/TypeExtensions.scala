package org.psesd.srx.shared.core.extensions

import java.util.UUID

import org.json4s.Xml.toJson
import org.json4s.jackson.JsonMethods._

import scala.xml.Node

/** Extensions for Scala primitive types.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object TypeExtensions {

  implicit class ArrayExtensions[T](val a: Array[T]) {

    def isNullOrEmpty: Boolean = Option(a).isEmpty || a.isEmpty

  }

  implicit class StringExtensions(val s: String) {

    def isUuid: Boolean = {
      if (s.isNullOrEmpty) {
        false
      } else {
        try {
          UUID.fromString(s)
          true
        } catch {
          case _: Throwable => false
        }
      }
    }

    def isNullOrEmpty: Boolean = Option(s).isEmpty || s.trim.isEmpty

    def trimPrecedingSlash: String = {
      if (!s.isNullOrEmpty && s.startsWith("/")) {
        s.stripPrefix("/")
      } else {
        s
      }
    }

    def trimTrailingSlash: String = {
      if (!s.isNullOrEmpty && s.endsWith("/")) {
        s.stripSuffix("/")
      } else {
        s
      }
    }

  }

  implicit class XmlNodeExtensions(val n: Node) {
    val lineWidth = 1000
    val printer = new scala.xml.PrettyPrinter(lineWidth, 2)

    def toJsonString: String = {
      pretty(render(toJson(n)))
    }

    def toXmlString: String = {
      printer.format(n)
    }
  }

}
