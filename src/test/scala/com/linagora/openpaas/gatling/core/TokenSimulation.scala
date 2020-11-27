package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.core.LoginSteps.login
import com.linagora.openpaas.gatling.core.TokenSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class TokenSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS token retrieval")
    .feed(feeder.circular())
    .exec(login)
    .exec(getProfile())
    .during(ScenarioDuration) {
      exec(retrieveAuthenticationToken)
        .pause(1 second)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
