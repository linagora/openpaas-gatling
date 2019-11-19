package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import com.linagora.openpaas.gatling.unifiedinbox.scenari.SendEmailScenari
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class SendEmailScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS send email")
    .exec(createGatlingTestDomainIfNotExist)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(provisionMessages)
    .pause(5 second)
    .during(ScenarioDuration) {
      exec(SendEmailScenari.generate())
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
