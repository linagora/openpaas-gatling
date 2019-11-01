package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.chat.ChannelsSteps._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class ListChannelsScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS chat channel listing")
    .exec(createGatlingTestDomainIfNotExist)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(createChannel())
        .pause(1 second)
        .exec(listChannelsForUser())
        .pause(1 second)
        .exec(listChannels())
        .pause(1 second)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
