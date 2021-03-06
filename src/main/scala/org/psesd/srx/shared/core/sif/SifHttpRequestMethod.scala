package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of SIF-supported HTTP request methods.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SifHttpRequestMethod extends ExtendedEnumeration {
  type SifHttpRequestMethod = Value
  val Delete = Value("DELETE")
  val Get = Value("GET")
  val Post = Value("POST")
  val Put = Value("PUT")
}
