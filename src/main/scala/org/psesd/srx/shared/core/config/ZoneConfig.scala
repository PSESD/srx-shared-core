package org.psesd.srx.shared.core.config

import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyOrWhitespaceException, EnvironmentException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif._

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

  val zoneConfigXml = getZoneConfigXml(serviceName)

  // nav to resource type="xSRE" and grab config data
  private val xsreConfigXml = (zoneConfigXml.get \ "resource").find(r => (r \ "@type").text.toLowerCase() == "xsre")
  if(xsreConfigXml.isEmpty) {
    throw new EnvironmentException("XSRE configuration missing for zone '%s'.".format(zoneId))
  }

  val cacheBucketName: String = (xsreConfigXml.get \ "cache" \ "bucketName").textRequired("cache.bucketName")
  val cachePath: String = (xsreConfigXml.get \ "cache" \ "path").textRequired("cache.path")
  val schemaPath: String = (xsreConfigXml.get \ "schema" \ "path").textRequired("schema.path")
  val schemaRootFileName: String = (xsreConfigXml.get \ "schema" \ "rootFileName").textRequired("schema.rootFileName")

  def getZoneConfigXml(serviceName: String) = {
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

    zoneConfigXml
  }
}
