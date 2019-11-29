package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.chat.ChannelsSteps._
import com.linagora.openpaas.gatling.core.LoginSteps.login
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class CreateChannelSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS chat channel creation")
    .feed(feeder.circular())
    .exec(login)
    .exec(getProfile())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(createChannel())
        .pause(1 second)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
