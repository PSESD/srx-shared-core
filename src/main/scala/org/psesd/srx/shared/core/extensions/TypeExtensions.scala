package org.psesd.srx.shared.core.extensions

import java.util.UUID

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

  implicit class StringExtensions(val s: String) {

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

    def trimPrecedingSlash: String = {
      if(s.startsWith("/")) {
        s.stripPrefix("/")
      } else {
        s
      }
    }

    def trimTrailingSlash: String = {
      if(s.endsWith("/")) {
        s.stripSuffix("/")
      } else {
        s
      }
    }

  }

}
