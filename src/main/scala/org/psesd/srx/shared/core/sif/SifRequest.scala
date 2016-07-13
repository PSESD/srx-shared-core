package org.psesd.srx.shared.core.sif

import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif.SifAccept.SifAccept
import org.psesd.srx.shared.core.sif.SifMessageType.SifMessageType
import org.psesd.srx.shared.core.sif.SifRequestAction.SifRequestAction
import org.psesd.srx.shared.core.sif.SifRequestType.SifRequestType

/** Represents a SIF request.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class SifRequest(val authorization: SifAuthorization, timestamp: SifTimestamp) extends SifMessage(timestamp) {
  if (authorization == null) {
    throw new ArgumentNullException("authorization parameter")
  }

  var accept: Option[SifAccept] = Option(SifAccept.Xml)
  var generatorId: Option[String] = None
  var messageId: Option[SifMessageId] = None
  var messageType: Option[SifMessageType] = Option(SifMessageType.Request)
  var requestAction: Option[SifRequestAction] = None
  var requestType: Option[SifRequestType] = Option(SifRequestType.Immediate)
  var uri: Option[SifUri] = None
}
