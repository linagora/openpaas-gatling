package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.core.TokenSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class TokenScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS token retrieval")
    .exec(createGatlingTestDomainIfNotExist)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(retrieveTokenWithAuth)
        .pause(1 second)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
