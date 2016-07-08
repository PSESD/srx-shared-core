package org.psesd.srx.shared.core.logging

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder, HttpClients}
import org.psesd.srx.shared.core.config.Environment

/** Handles interactions with the Rollbar API.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object RollbarClient {
  private final val RollbarUrlKey = "ROLLBAR_URL"

  private val rollbarUrl = Environment.getProperty(RollbarUrlKey)

  def SendItem(rollbarMessage: String): Int = {
    val post = new HttpPost(rollbarUrl)
    post.setHeader("content-type", "application/json")
    post.setEntity(new StringEntity(rollbarMessage))

    val httpclient: CloseableHttpClient = HttpClients.createDefault()

    val result = httpclient.execute(post)
    try {
      result.getStatusLine.getStatusCode
    } finally {
      result.close()
    }
  }

}
