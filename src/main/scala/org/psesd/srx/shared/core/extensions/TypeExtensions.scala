package org.psesd.srx.shared.core.extensions

import java.io.{PrintWriter, StringWriter}
import java.util.UUID

import org.json4s.JValue
import org.json4s.Xml.toJson
import org.json4s.jackson.JsonMethods._
import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException

import scala.xml.{Node, NodeSeq, XML}

/** Extensions for Scala primitive types.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object TypeExtensions {

  implicit class ArrayExtensions[T](val a: Array[T]) {

    def isNullOrEmpty: Boolean = Option(a).isEmpty || a.isEmpty

  }

  implicit class ExceptionExtensions[T](val e: Exception) {

    def getFormattedStackTrace: String = {
      val sw = new StringWriter
      val pw = new PrintWriter(sw)
      e.printStackTrace(pw)
      sw.toString
    }

  }

  implicit class JValueExtensions(val j: JValue) {

    def toJsonString = {
      pretty(render(j))
    }

    def toXml: Node = {
      org.json4s.Xml.toXml(j).head
    }

  }

  implicit class StringExtensions(val s: String) {

    def isJson: Boolean = {
      try{
        s.toJson
        true
      } catch {
        case e: Exception =>
          false
      }
    }

    def isNullOrEmpty: Boolean = Option(s).isEmpty || s.trim.isEmpty

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

    def isXml: Boolean = {
      try{
        s.toXml
        true
      } catch {
        case e: Exception =>
          false
      }
    }

    def toJson: JValue = {
      parse(s)
    }

    def toXml: Node = {
      XML.loadString(s)
    }

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

  implicit class ThrowableExtensions[T](val t: Throwable) {

    def getFormattedStackTrace: String = {
      val sw = new StringWriter
      val pw = new PrintWriter(sw)
      t.printStackTrace(pw)
      sw.toString
    }

  }

  implicit class XmlNodeExtensions(val n: Node) {
    val lineWidth = 1000
    val printer = new scala.xml.PrettyPrinter(lineWidth, 2)

    def toJsonString: String = {
      pretty(render(toJson(n)))
    }

    def toJsonStringNoRoot: String = {
      val rootString = n.toJsonString
      val rootLabel = n.label
      val delta = {
        if(rootString.startsWith("{\r\n")) 10 else 8
      }
      rootString.substring(delta + rootLabel.length, rootString.length - 1).trim
    }

    def toXmlString: String = {
      printer.format(n)
    }
  }

  implicit class XmlNodeSeqExtensions(val n: NodeSeq) {

    def textOption: Option[String] = {
      val textValue = n.text
      if (textValue.isNullOrEmpty) {
        None
      } else {
        Some(textValue)
      }
    }

    def textRequired(path: String): String = {
      val textValue = n.text
      if (textValue.isNullOrEmpty) {
        throw new ArgumentNullOrEmptyOrWhitespaceException(path)
      }
      textValue
    }

  }

}
