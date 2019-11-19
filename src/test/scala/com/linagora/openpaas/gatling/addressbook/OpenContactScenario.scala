package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.AddressBooksSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactScenari

import scala.concurrent.duration.DurationInt

class OpenContactScenario extends Simulation{
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS opening a contact")
    .exec(createGatlingTestDomainIfNotExist)
    .pause(1 second)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(provisionContacts)
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(OpenContactScenari.generate())
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
