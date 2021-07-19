package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import WebSocketSteps._
import com.linagora.openpaas.gatling.core.LoginSteps.login
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.TokenSteps._
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class WebSocketSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS token retrieval")
    .feed(feeder.circular())
    .pause(1 second)
    .exec(login())
    .exec(getProfile())
    .pause(1 second)
    .exec(openWsConnection())

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
