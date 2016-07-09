package org.psesd.srx.shared.core.extensions

/** Extensions for Scala primitive types.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object TypeExtensions {

  implicit class StringExtensions(val s: String) {

    def isNullOrEmpty: Boolean = Option(s).isEmpty || s.trim.isEmpty

  }

  implicit class ArrayExtensions[T](val a: Array[T]) {

    def isNullOrEmpty: Boolean = Option(a).isEmpty || a.isEmpty

  }

}
