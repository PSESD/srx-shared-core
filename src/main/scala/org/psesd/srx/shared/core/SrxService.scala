package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyException}

import scala.xml.Node

/** Represents SRX service.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class SrxService(val service: SrxServiceComponent, val buildComponents: List[SrxServiceComponent]) {
  if (service == null) {
    throw new ArgumentNullException("service parameter")
  }
  if (buildComponents == null || buildComponents.isEmpty) {
    throw new ArgumentNullOrEmptyException("buildComponents parameter")
  }

  def toXml: Node = {
    <service>
      <name>{service.name}</name>
      <version>{service.version}</version>
      <build>{buildComponents.map(c => getBuildComponentXml(c))}</build>
      {SystemInfo.toXml}
    </service>
  }

  private def getBuildComponentXml(component: SrxServiceComponent): Node = {
    <component>
      <name>{component.name}</name>
      <version>{component.version}</version>
    </component>
  }
}
