package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.collection.mutable.ArrayBuffer
import scala.xml.Node

/** Represents a SIF DELETE response.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifDeleteResponse {

  private val results = ArrayBuffer[DeleteResult]()

  def addResult(id: String, statusCode: Int): SifDeleteResponse = {
    if(id.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("id parameter")
    }
    results += new DeleteResult(id, statusCode)
    this
  }

  def toXml: Node = {
    <deleteResponse>{getResults}</deleteResponse>
  }

  def getResults: Node = {
    if (results.isEmpty) null else <deletes>{results.map(result => result.toXml)}</deletes>
  }

  private class DeleteResult(val id: String, val statusCode: Int) {
    def toXml: Node = <delete id={id} statusCode={statusCode.toString}/>
  }

}

object SifDeleteResponse {
  def apply(): SifDeleteResponse = new SifDeleteResponse()
}
