package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.UsersSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning._
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class RetrieveUserScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS chat channel creation")
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(feeder.selectUsernameStep())
        .exec(findUserIdByUsername)
        .pause(1 second)
        .exec(getOtherUserProfile)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
