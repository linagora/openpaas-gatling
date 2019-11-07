package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.unifiedinbox.TemplatesSteps._
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.core.TokenSteps.generateJwtToken

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
    .pause(1 second)
    .exec(provisionMessages)
    .pause(5 second)
    .exec(loadOpeningEventTemplates)
    .pause(1 second)
    .exec(generateJwtToken)
    .pause(1 second)
    .exec(generateJwtToken)
    .pause(1 second)
    .exec(generateJwtToken)
    .pause(1 second)
    .exec(generateJwtToken)
    .pause(1 second)
    .exec(generateJwtToken)
    .pause(1 second)
    .exec(getVacationResponse)
    .pause(1 second)
    .exec(getMailboxes)
    .pause(1 second)
    .exec(getMailboxes)
    .pause(1 second)
    .exec(getVacationResponse)
    .pause(1 second)
    .exec(getMailboxes)
    .pause(1 second)
    .exec(getMessageList)
    .pause(1 second)
    .exec(loadOpeningComposerTemplates)
    .pause(1 second)
    .exec(uploadAttachment)
    .pause(1 second)
    .exec(sendMessageWithAttachment)
    .pause(1 second)
    .exec(logout)
    .pause(1 second)

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
