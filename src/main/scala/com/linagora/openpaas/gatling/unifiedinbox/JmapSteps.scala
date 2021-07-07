package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.TokenSteps._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.unifiedinbox.SessionKeys._
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.json.Json
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import org.apache.james.gatling.jmap.draft.JmapMailbox.getSystemMailboxesChecks
import org.apache.james.gatling.jmap.draft.JmapMessages.{JmapParameters, NO_PARAMETERS, getRandomMessageChecks, messageIdSessionParam, nonEmptyListMessagesChecks, openpaasInboxOpenMessageProperties, openpaasListMessageParameters, previewMessageProperties}
import org.apache.james.gatling.jmap.draft.{JmapChecks, JmapHttp, JmapMailbox}

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
      .check(nonEmptyListMessagesChecks: _*))
      .exec { session => session.set("messageIdRead", {
          Vector(session("messageIds").as[Vector[String]].head)
        })
      }.exec { session => session.set("messageIdReadUpdate", {
          session("messageIdRead").as[Vector[String]].head
        })
      }

  def getIdleMessageList: ChainBuilder =
    exec(listMessages(idleFetchParameters())
      .check(nonEmptyListMessagesChecks: _*))

  def idleFetchParameters(mailboxKey: String = "inboxID", afterDate: String = "afterDate"): JmapParameters = {
    val mailboxes = List(mailboxKey).map(key => s"$${$key}")
    Map("filter" -> Map(
        "inMailboxes" -> mailboxes,
        "text" -> null,
        "after" -> s"$${afterDate}"),
      "sort" -> Seq("date desc"),
      "collapseThreads" -> false,
      "fetchMessages" -> false,
      "position" -> 0,
      "limit" -> 30
    )
  }

  def getMessages(): HttpRequestBuilder =
    getMessages(previewMessageProperties)
      .check(getRandomMessageChecks: _*)
      .check(jsonPath("$[0][1].list[0].date").saveAs("afterDate"))

  def readMessage(): HttpRequestBuilder =
    getMessages(openpaasInboxOpenMessageProperties, "messageIdRead")
      .check(getRandomMessageChecks: _*)

  def getMessages(properties: List[String], messageIdsKey: String = "messageIds"): HttpRequestBuilder = {
    authenticatedQueryWithJwtToken("getMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "getMessages",
          {
            "ids": $${$messageIdsKey.jsonStringify()},
            "properties": ${Json.stringify(properties)}
          },
          "#0"
          ]]"""))
  }

  def markAsRead(messageIdKey: String = "messageIdReadUpdate") = {
    authenticatedQueryWithJwtToken("setMessages", "/jmap")
      .body(StringBody(
        s"""[[
          "setMessages",
          {
            "update": {
              "${messageIdKey}" : {
                "isUnread": false
              }
            }
          },
          "#0"
          ]]"""))
  }

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
