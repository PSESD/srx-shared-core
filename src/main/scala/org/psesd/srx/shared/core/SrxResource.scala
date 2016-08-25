package org.psesd.srx.shared.core

import scala.xml.Node

/** SRX resource interface.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
trait SrxResource {
  protected def optional(value: String, xml: Node): Node = {
    if(value == null || value.isEmpty) null else xml
  }

  protected def isEmpty: Boolean = {false}
}
