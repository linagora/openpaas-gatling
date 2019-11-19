package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.AddressBooksSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactScenari
import com.linagora.openpaas.gatling.calendar.CalendarsSteps.provisionEvents
import com.linagora.openpaas.gatling.calendar.scenari.SearchEventsScenari
import com.linagora.openpaas.gatling.chat.scenari.SendMessageScenari
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps.provisionMessages
import com.linagora.openpaas.gatling.unifiedinbox.scenari.SendEmailScenari

import scala.concurrent.duration.DurationInt

class OpenPaaSMixScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS mix scenarios")
    .exec(createGatlingTestDomainIfNotExist)
    .pause(1 second)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(provisionMessages)
    .pause(5 second)
    .exec(provisionEvents)
    .pause(1 second)
    .exec(provisionContacts)
    .pause(1 second)
    .during(ScenarioDuration) {
      randomSwitch(
        25.0 -> exec(SendMessageScenari.generate()),
        25.0 -> exec(SearchEventsScenari.generate()),
        25.0 -> exec(SendEmailScenari.generate()),
        25.0 -> exec(OpenContactScenari.generate())
      ).pause(7500 milliseconds, 15 seconds)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
