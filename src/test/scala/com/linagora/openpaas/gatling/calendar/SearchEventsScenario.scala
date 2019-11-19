package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.CalendarsSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import com.linagora.openpaas.gatling.core.DomainSteps._
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.calendar.scenari.SearchEventsScenari

import scala.concurrent.duration.DurationInt

class SearchEventsScenario extends  Simulation{
  val feeder = new RandomFeeder(UserCount)
  val eventUuidFeeder = Iterator.continually(Map("eventUuid" -> randomUuidString))

  val scn = scenario("Testing OpenPaaS calendar searching")
    .exec(createGatlingTestDomainIfNotExist)
    .pause(1 second)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(provisionEvents)
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(SearchEventsScenari.generate())
    }

    setUp(
      scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
