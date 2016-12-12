package org.psesd.srx.shared.core.config

import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyOrWhitespaceException, EnvironmentException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif._

import scala.xml.Node

/** Represents zone-specific configuration and xSRE schema.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  */
class ZoneConfig(val zoneId: String, serviceName: String) {
  if (zoneId.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("zoneId")
  }

  if (serviceName.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("serviceName")
  }

  val zoneConfigXml: Node = getZoneConfigXml(serviceName)

  private def getZoneConfigXml(serviceName: String) = {
    val resource = "%s/%s".format("srxZoneConfig", zoneId)
    val sifRequest = new SifRequest(Environment.srxProvider, resource)
    sifRequest.requestId = Some(SifMessageId().toString)
    sifRequest.generatorId = Some(serviceName)

    val response = SifConsumer().query(sifRequest)
    if (!response.isValid) {
      throw response.exceptions.head
    }

    val zoneConfigXml = response.getBodyXml
    if(zoneConfigXml.isEmpty) {
      throw new EnvironmentException("Configuration missing for zone '%s'.".format(zoneId))
    }
    if((zoneConfigXml.get \ "description").nonEmpty) {
      throw new EnvironmentException((zoneConfigXml.get \ "description").text)
    }

    zoneConfigXml.get
  }
}
