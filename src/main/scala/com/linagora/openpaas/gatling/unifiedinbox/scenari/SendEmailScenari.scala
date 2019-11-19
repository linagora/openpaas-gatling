package com.linagora.openpaas.gatling.unifiedinbox.scenari

import com.linagora.openpaas.gatling.unifiedinbox.TemplatesSteps._
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.core.TokenSteps.{generateJwtToken, retrieveAuthenticationToken}
import com.linagora.openpaas.gatling.core.WebSocketSteps._

object SendEmailScenari {

  def generate() =
    exec(loadLoginTemplates)
      .exec(login())
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
      .exec(getVacationResponse)
      .exec(getMailboxes)
      .exec(getMessageList)
      .exec(loadOpeningComposerTemplates)
      .exec(uploadAttachment)
      .exec(sendMessageWithAttachment)
      .exec(closeWsConnection)
      .exec(logout)
}
