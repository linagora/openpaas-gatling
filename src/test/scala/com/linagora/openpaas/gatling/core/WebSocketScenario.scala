package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import WebSocketSteps._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.core.LoginSteps.login
import com.linagora.openpaas.gatling.core.TokenSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class WebSocketScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS token retrieval")
    .exec(createGatlingTestDomainIfNotExist)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(login())
    .pause(1 second)
    .exec(retrieveAuthenticationToken)
    .pause(1 second)
    .exec(getSocketId)
    .exec(registerSocketNamespaces)
    .exec(openWsConnection())

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
