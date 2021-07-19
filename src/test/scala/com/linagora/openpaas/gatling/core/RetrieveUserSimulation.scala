package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.core.UsersSteps._
import com.linagora.openpaas.gatling.core.LoginSteps.login
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.utils.OtherUserSelector
import com.linagora.openpaas.gatling.provisionning.SessionKeys.{OtherUsername, UsernameSessionParam}
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class RetrieveUserSimulation extends Simulation {
  private val feeder = csv("users.csv")
  private val randomFeeder = feeder.copy().random.apply()

  val scn = scenario("Testing OpenPaaS chat channel creation")
    .exec(createGatlingTestDomainIfNotExist)
    .feed(feeder.circular())
    .exec(login())
    .exec(getProfile())
    .during(ScenarioDuration) {
      exec(OtherUserSelector.selectFrom(randomFeeder))
        .exec(findUserIdByUsername)
        .pause(1 second)
        .exec(getOtherUserProfile)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
