package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration
import org.psesd.srx.shared.core.sif.SifHttpRequestMethod.SifHttpRequestMethod

/** Enumeration of SIF requestAction header values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifRequestAction extends ExtendedEnumeration {
  type SifRequestAction = Value
  val Create = Value("CREATE")
  val Delete = Value("DELETE")
  val Query = Value("QUERY")
  val Update = Value("UPDATE")

  def fromHttpMethod(method: SifHttpRequestMethod): SifRequestAction = {
    method match {
      case SifHttpRequestMethod.Post =>
        Create

      case SifHttpRequestMethod.Delete =>
        Delete

      case SifHttpRequestMethod.Get =>
        Query

      case SifHttpRequestMethod.Put =>
        Update

      case _ =>
        null
    }
  }

  def getSuccessStatusCode(action: SifRequestAction): Int = {
    action match {
      case Create =>
        201
      case _ =>
        200
    }
  }
}
