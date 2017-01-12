package org.psesd.srx.shared.core.sif

/** Collection of Sif Request Parameters.
  * @version 1.0
  * @since 1.0
  * @author Kristy Overton (iTrellis, LLC)
  */
class SifRequestParameterCollection(params: List[SifRequestParameter]) {
  val parameters: List[SifRequestParameter] = params

  //copied from SrxResourceService.getRequestParameter
  def apply(name:String) : Option[String] = {
    if (parameters != null && parameters.nonEmpty) {
      val parameter = parameters.find(p => p.key.toLowerCase == name.toLowerCase()).orNull
      if (parameter != null) {
        Some(parameter.value)
      } else {
        None
      }
    } else {
      None
    }
  }

  def getHeaders(): String = {
      val sb = new StringBuilder("")
      var sep = ""
      for (p <- parameters) {
        sb.append("%s%s=%s".format(sep, p.key, p.value))
        sep = ";"
      }
      sb.toString
  }
}

object SifRequestParameterCollection {
  def apply(parameters: List[SifRequestParameter]) = new SifRequestParameterCollection(parameters)
}

