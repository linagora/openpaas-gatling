package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.chat.ChannelsSteps._
import com.linagora.openpaas.gatling.core.LoginSteps.login
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.UsersSteps._
import com.linagora.openpaas.gatling.utils.OtherUserSelector
import com.linagora.openpaas.gatling.provisionning.SessionKeys.{OtherUsername, UsernameSessionParam}
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class CreatePrivateChannelSimulation extends Simulation {
  private val feeder = csv("users.csv")
  private val randomFeeder = feeder.copy().random.apply()

  val scn = scenario("Testing OpenPaaS private conversation creation")
    .feed(feeder.circular)
    .exec(login)
    .exec(getProfile())
    .during(ScenarioDuration) {
      exec(OtherUserSelector.selectFrom(randomFeeder))
        .exec(findUserIdByUsername)
        .pause(1 second)
        .exec(createPrivateChannel)
        .pause(1 second)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
