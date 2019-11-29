package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.LoginSteps.login
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.chat.scenari.SendMessageScenari
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class SendMessageSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS chat send a message")
    .feed(feeder.circular())
    .exec(login)
    .exec(getProfile())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(SendMessageScenari.generate())
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
