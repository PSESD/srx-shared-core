package org.psesd.srx.shared.core.extensions

import org.http4s.EntityBody
import scodec.bits.ByteVector

import scalaz.stream.Process._

/** Extensions for HTTP types.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object HttpTypeExtensions {

  implicit class EntityBodyExtensions(val eb: EntityBody) {

    def value: String = {
      try {
        val array = eb.runLog.run.reduce(_ ++ _).toArray
        new String(array.map(_.toChar))
      } catch {
        case e: Exception =>
          ""
      }
    }

  }

  implicit class BodyStringExtensions(val s: String) {

    def toEntityBody: EntityBody = {
      emit(s).map(str => ByteVector(str.getBytes))
    }
  }

}
