package org.psesd.srx.shared.core.sif

import scala.xml.Node

/** Represents a SIF error response.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SifError(val code: Int, val scope: String, val message: String, val description: String) {
  val id = SifMessageId()

  def toXml: Node = {
    <error id={id.toString}>
      <code>{code.toString}</code>
      <scope>{scope}</scope>
      <message>{message}</message>
      <description>{description}</description>
    </error>
  }
}
