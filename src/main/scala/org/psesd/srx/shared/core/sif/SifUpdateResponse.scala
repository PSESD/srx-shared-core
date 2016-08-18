package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.collection.mutable.ArrayBuffer
import scala.xml.Node

/** Represents a SIF UPDATE response.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifUpdateResponse {

  private val results = ArrayBuffer[UpdateResult]()

  def addResult(id: String, statusCode: Int): SifUpdateResponse = {
    if(id.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("id parameter")
    }
    results += new UpdateResult(id, statusCode)
    this
  }

  def toXml: Node = {
    <updateResponse>{getResults}</updateResponse>
  }

  def getResults: Node = {
    if (results.isEmpty) null else <updates>{results.map(result => result.toXml)}</updates>
  }

  private class UpdateResult(val id: String, val statusCode: Int) {
    def toXml: Node = <update id={id} statusCode={statusCode.toString}/>
  }

}

object SifUpdateResponse {
  def apply(): SifUpdateResponse = new SifUpdateResponse()
}
