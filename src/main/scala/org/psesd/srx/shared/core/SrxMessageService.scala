package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif._

/** Provides SRX system message service methods.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object SrxMessageService {

  def createMessage(generatorId: String, srxMessage: SrxMessage): SifResponse = {
    val sifRequest = new SifRequest(Environment.srxProvider, CoreResource.SrxMessages.toString)
    sifRequest.requestId = Some(SifMessageId().toString)
    sifRequest.generatorId = Some(generatorId)
    sifRequest.contentType = Some(SifContentType.Json)
    sifRequest.accept = Some(SifContentType.Json)
    sifRequest.serviceType = Some(SifServiceType.Object)
    sifRequest.messageType = Some(SifMessageType.Request)
    sifRequest.requestType = Some(SifRequestType.Immediate)
    sifRequest.body = Some(srxMessage.toXml.toJsonString)

    new SifConsumer().create(sifRequest)
  }

}
