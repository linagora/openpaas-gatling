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
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

object SendMessageScenari {

  def generate() =
    exec(loadLoginTemplates)
      .exec(login)
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
      .pause(humanActionDelay() second)
      .exec(markUserAsReadAllMessages)
      .exec(getChannelMessages)
      .pause(humanActionDelay() second)
      .exec(getLogoForDomain)
      .exec(getSocketId)
      .exec(registerSocketNamespaces)
      .exec(openWsConnection())
      .pause(humanActionDelay() second)
      .exec(ws("Send ping message").sendText("""2probe""").await(5 second) {
        ws.checkTextMessage("check text ping")
      })
      .pause(humanActionDelay() second)
      .exec(ws("Send upgrade message").sendText("""5""").await(5 second) {
        ws.checkTextMessage("check text upgrade").check(jsonPath("$").is("40"))
      })
      .pause(humanActionDelay() second)
      .exec(ws("Send subscribe message").sendText("""42/chat,["subscribe","default"]""").await(5 second) {
        ws.checkTextMessage("check text subscribe")
      })
      .pause(humanActionDelay() second)
      .exec(ws("Send user_typing message").sendText(s"""42/chat,["message",{"text":"Hello","creator":"$${$UserId}","type":"user_typing","channel":"$${$ChannelId}"}]""").await(5 second) {
        ws.checkTextMessage("check text user_typing")
      })
      .pause(humanActionDelay() second)
      .exec(ws("Send text message").sendText(s"""42/chat,["message",{"text":"Hello","creator":"$${$UserId}","type":"text","channel":"$${$ChannelId}"}]""").await(5 second) {
        ws.checkTextMessage("check text send message")
      })
      .pause(humanActionDelay() second)
      .exec(closeWsConnection)
      .exec(logout)
}
