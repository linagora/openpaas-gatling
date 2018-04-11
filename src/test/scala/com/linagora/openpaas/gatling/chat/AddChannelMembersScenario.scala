package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.chat.ChannelsSteps._
import com.linagora.openpaas.gatling.core.UsersSteps.findUserIdByUsername
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import com.linagora.openpaas.gatling.core.UserSteps._
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class AddChannelMembersScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS chat channel members retrieval")
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(createChannel())
    .pause(1 second)
    .exec(listChannels())
    .pause(1 second)
    .exec(getProfile())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(feeder.selectUsernameStep())
        .exec(findUserIdByUsername)
        .exec(pickOneChannel)
        .pause(1 second)
        .exec(addChannelMember)
        .pause(1 second)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
