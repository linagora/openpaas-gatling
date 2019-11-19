package com.linagora.openpaas.gatling.chat.scenari

import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.chat.SessionKeys._
import com.linagora.openpaas.gatling.chat.ChannelsSteps._
import com.linagora.openpaas.gatling.chat.Utils._
import com.linagora.openpaas.gatling.chat.TemplatesSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.TokenSteps._
import com.linagora.openpaas.gatling.core.NotificationSteps._
import com.linagora.openpaas.gatling.core.WebSocketSteps._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

object SendMessageScenari {

  def generate() =
    exec(loadLoginTemplates)
      .exec(login())
      .exec(loadTemplatesWhenRedirectingToChatPageAfterLogin)
      .exec(getDomain)
      .exec(getUnreadNotifications)
      .exec(listPrivateChannelsForUser)
      .exec(listChannelsForUser)
      .exec(listChannelsForUser)
      .exec(listPrivateChannelsForUser)
      .exec(listChannelsForUser)
      .exec(listPrivateChannelsForUser)
      .exec(retrieveAuthenticationToken)
      .exec(getChannelIdByName("general"))
      .exec(getChannelDetails)
      .exec(getChannelDetails)
      .exec(getChannelDetails)
      .exec(getChannelDetails)
      .exec(markUserAsReadAllMessages)
      .exec(getChannelMessages)
      .exec(getLogoForDomain)
      .exec(getSocketId)
      .exec(registerSocketNamespaces)
      .exec(openWsConnection())
      .exec(ws("Send ping message").sendText("""2probe""").await(5 second) {
        ws.checkTextMessage("check text ping")
      })
      .exec(ws("Send upgrade message").sendText("""5""").await(5 second) {
        ws.checkTextMessage("check text upgrade").check(jsonPath("$").is("40"))
      })
      .exec(ws("Send subscribe message").sendText("""42/chat,["subscribe","default"]""").await(5 second) {
        ws.checkTextMessage("check text subscribe")
      })
      .exec(ws("Send user_typing message").sendText(s"""42/chat,["message",{"text":"Hello","creator":"$${$UserId}","type":"user_typing","channel":"$${$ChannelId}"}]""").await(5 second) {
        ws.checkTextMessage("check text user_typing")
      })
      .exec(ws("Send text message").sendText(s"""42/chat,["message",{"text":"Hello","creator":"$${$UserId}","type":"text","channel":"$${$ChannelId}"}]""").await(5 second) {
        ws.checkTextMessage("check text send message")
      })
      .exec(closeWsConnection)
      .exec(logout)
}
