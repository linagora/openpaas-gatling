package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.chat.WebSocketSteps._
import com.linagora.openpaas.gatling.core.TokenSteps._
import com.linagora.openpaas.gatling.core.UserSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class WebSocketScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS token retrieaval")
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(getProfile)
    .pause(1 second)
    .exec(retrieveToken)
    .pause(1 second)
    .exec(openChatConnection)

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
