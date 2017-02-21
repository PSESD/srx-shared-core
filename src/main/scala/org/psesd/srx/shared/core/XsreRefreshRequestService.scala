package org.psesd.srx.shared.core

import java.util.UUID

import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.sif._


/**
  * Created by Kristy Overton on 2/21/2017.
  */
object XsreRefreshRequestService {

  def sendRequest(zone: SifZone, studentId: String, generatorId: String) : SifResponse = {

      val uri = Environment.srxEnvironmentUrl + "/xSresRefresh;zoneId=" + zone.toString + ";contextId=DEFAULT"
      val sifRequest = new SifRequest(Environment.srxProvider, uri, zone, SifContext()) {
        body = Some("<request><studentIds><studentId>" + studentId + "</studentId></studentIds></request>")
      }

    try {
      new SifConsumer().create(sifRequest)
    }
    catch {
      case _ : Throwable =>
        new SifResponse(sifRequest.timestamp, sifRequest.messageId.getOrElse(new SifMessageId(UUID.randomUUID())), (sifRequest.messageType).get, sifRequest) {
          statusCode = 500
        }
    }
  }

}
