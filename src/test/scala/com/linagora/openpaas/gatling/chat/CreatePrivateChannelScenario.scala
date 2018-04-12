package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.chat.ChannelsSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import com.linagora.openpaas.gatling.utils.Debug._
import com.linagora.openpaas.gatling.core.UsersSteps._
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class CreatePrivateChannelScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS private conversation creation")
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(feeder.selectUsernameStep())
        .exec(findUserIdByUsername)
        .pause(1 second)
        .exec(createPrivateChannel)
        .pause(1 second)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
