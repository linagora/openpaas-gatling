package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.core.TokenSteps._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.unifiedinbox.SessionKeys._
import com.linagora.openpaas.gatling.Configuration._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.jmap.JmapMailbox.{getSystemMailboxesChecks}
import org.apache.james.gatling.jmap.JmapMessages.{JmapParameters, NO_PARAMETERS, messageIdSessionParam, openpaasListMessageParameters}
import org.apache.james.gatling.jmap.{JmapChecks, JmapHttp, JmapMailbox}
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.json.Json

import scala.concurrent.duration.DurationInt

object JmapSteps {
  def authenticatedQueryWithJwtToken(requestName: String, endPoint: String): HttpRequestBuilder =
    http(requestName)
      .post(s"$JmapBaseUrl$endPoint")
      .header("Authorization", s"Bearer $${$JwtToken}")
      .header(JmapHttp.ACCEPT_JSON_KEY, JmapHttp.ACCEPT_JSON_VALUE)

  def getVacationResponse: HttpRequestBuilder = {
    authenticatedQueryWithJwtToken("getVacationResponse", "/jmap")
      .body(StringBody("""[["getVacationResponse",{},"#0"]]"""))
      .check(status in(200, 304))
  }

  def getMailboxes: HttpRequestBuilder =
    authenticatedQueryWithJwtToken("getMailboxes", "/jmap")
      .body(StringBody("""[["getMailboxes", {}, "#0"]]"""))
      .check(status in(200, 304))
      .check(JmapChecks.noError)
      .check(JmapMailbox.saveInboxAs("inboxID"): _*)
      .check(getSystemMailboxesChecks: _*)

  def listMessages(queryParameters: JmapParameters = NO_PARAMETERS) =
    authenticatedQueryWithJwtToken("listMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "getMessageList",
            ${Json.stringify(queryParameters)}
           ,
          "#0"
          ]]"""))

  def getMessageList: ChainBuilder =
    exec(listMessages(openpaasListMessageParameters("inboxID"))
      .check(status in(200, 304))
      .check(JmapChecks.noError))

  def sendMessages() =
    authenticatedQueryWithJwtToken("sendMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "setMessages",
          {
            "create": {
              "$messageIdSessionParam" : {
                "from": {"name":"$UsernameSessionParam}", "email": "$UsernameSessionParam}"},
                "to":  [{"name":"$UsernameSessionParam}", "email": "$UsernameSessionParam}"}],
                "textBody": "Test text body",
                "subject": "Gatling test",
                "mailboxIds": ["$${${JmapMailbox.outboxMailboxIdSessionParam}}"]
              }
            }
          },
          "#0"
          ]]"""))

  def sendMessageWithAttachment: HttpRequestBuilder =
    authenticatedQueryWithJwtToken("sendMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "setMessages",
          {
            "create": {
              "$messageIdSessionParam" : {
                "from": {"name":"$UsernameSessionParam}", "email": "$UsernameSessionParam}"},
                "to":  [{"name":"$UsernameSessionParam}", "email": "$UsernameSessionParam}"}],
                "textBody": "Test text body",
                "subject": "Gatling test",
                "mailboxIds": ["$${${JmapMailbox.outboxMailboxIdSessionParam}}"],
                "attachments": [{
                  "blobId": "$${$BlobId}",
                  "type": "text/plain",
                  "name": "attachment_200kb.txt",
                  "size": 204800,
                  "url": "${JmapBaseUrl}/download/$${$BlobId}/attachment_200kb.txt",
                  "isInline": false
                }]
              }
            }
          },
          "#0"
          ]]"""))

  def uploadAttachment: HttpRequestBuilder =
    authenticatedQueryWithJwtToken("Upload attachment", "/upload")
    .header("Content-Length", "204800") //200KB
    .header("Content-Type", "text/plain")
    .body(RawFileBody("attachment_200kb.txt"))
    .check(status is 201)
    .check(jsonPath("$.blobId").saveAs(BlobId))

  def provisionMessages: ChainBuilder = {
    val messageUuidFeeder = Iterator.continually(Map("messageIdSessionParam" -> randomUuidString))

    group("Provisioning messages") {
      exec(generateJwtTokenWithAuth)
        .pause(1 second)
        .exec(getMailboxes)
        .repeat(EmailCount) {
          feed(messageUuidFeeder)
            .exec(sendMessages)
            .pause(1 second)
        }
    }
  }
}
