package com.linagora.openpaas.gatling.unifiedinbox.scenari

import com.linagora.openpaas.gatling.unifiedinbox.TemplatesSteps._
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.TokenSteps.{generateJwtToken, retrieveAuthenticationToken}
import com.linagora.openpaas.gatling.core.WebSocketSteps._
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay._

import io.gatling.core.Predef._
import scala.concurrent.duration.DurationInt

object SendEmailScenari {

  def generate() =
    exec(loadLoginTemplates)
      .exec(login)
      .exec(retrieveAuthenticationToken)
      .exec(getSocketId)
      .exec(registerSocketNamespaces)
      .exec(openWsConnection())
      .exec(loadOpeningEventTemplates)
      .exec(generateJwtToken)
      .exec(generateJwtToken)
      .exec(generateJwtToken)
      .exec(generateJwtToken)
      .exec(generateJwtToken)
      .exec(getVacationResponse)
      .exec(getMailboxes)
      .exec(getMailboxes)
      .pause(humanActionDelay() second)
      .exec(getVacationResponse)
      .exec(getMailboxes)
      .pause(humanActionDelay() second)
      .exec(getMessageList)
      .pause(humanActionDelay() second)
      .exec(loadOpeningComposerTemplates)
      .pause(humanActionDelay() second)
      .exec(uploadAttachment)
      .pause(humanActionDelay() second)
      .exec(sendMessageWithAttachment)
      .exec(closeWsConnection)
      .pause(humanActionDelay() second)
      .exec(logout)
}
