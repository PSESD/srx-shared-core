package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.collection.mutable.ArrayBuffer
import scala.xml.Node

/** Represents a SIF CREATE response.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifCreateResponse {

  private val results = ArrayBuffer[CreateResult]()

  def addResult(id: String, statusCode: Int): SifCreateResponse = {
    if(id.isNullOrEmpty) {
      throw new ArgumentNullOrEmptyOrWhitespaceException("id parameter")
    }
    results += new CreateResult(id, results.length + 1, statusCode)
    this
  }

  def toXml: Node = {
    <createResponse>{getResults}</createResponse>
  }

  def getResults: Node = {
    if (results.isEmpty) null else <creates>{results.map(result => result.toXml)}</creates>
  }

  private class CreateResult(val id: String, val advisoryId: Int, val statusCode: Int) {
    def toXml: Node = <create id={id} advisoryId={advisoryId.toString} statusCode={statusCode.toString}/>
  }

}

object SifCreateResponse {
  def apply(): SifCreateResponse = new SifCreateResponse()
}
