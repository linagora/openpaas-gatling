package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.CalendarsSteps._
import com.linagora.openpaas.gatling.core.DomainSteps.createGatlingTestDomainIfNotExist
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import com.linagora.openpaas.gatling.core.LoginSteps._
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class CreateEventScenario extends Simulation {
  val feeder = new RandomFeeder(UserCount)
  val eventUuidFeeder = Iterator.continually(Map("eventUuid" -> randomUuidString))

  val scn = scenario("Testing OpenPaaS calendar listing")
    .exec(createGatlingTestDomainIfNotExist)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(login())
    .pause(1 second)
    .during(ScenarioDuration) {
      feed(eventUuidFeeder)
      .exec(createEventOnDefaultCalendar())
        .pause(1 second)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
