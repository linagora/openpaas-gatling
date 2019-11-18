package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.unifiedinbox.TemplatesSteps._
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.core.TokenSteps.{generateJwtToken, retrieveAuthenticationToken}
import com.linagora.openpaas.gatling.core.WebSocketSteps._

import scala.concurrent.duration.DurationInt

class SendEmailScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS send email")
    .exec(createGatlingTestDomainIfNotExist)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(login())
    .exec(provisionMessages)
    .pause(5 second)
    .exec(retrieveAuthenticationToken)
    .exec(getSocketId)
    .exec(registerSocketNamespaces)
    .exec(openConnection())
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
    .exec(logout)

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
