package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.AddressBooksSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactScenari
import com.linagora.openpaas.gatling.calendar.EventSteps.provisionEvents
import com.linagora.openpaas.gatling.calendar.scenari.CalendarMixScenari
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps.provisionMessages
import com.linagora.openpaas.gatling.unifiedinbox.scenari.SendEmailScenari
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString

import scala.concurrent.duration.DurationInt

class OpenPaaSMixSimulation extends Simulation {
  val userFeeder = csv("users.csv")
  val eventUUIDFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))

  val scn = scenario("Testing OpenPaaS mix scenarios")
    .exec(createGatlingTestDomainIfNotExist)
    .pause(1 second)
    .feed(userFeeder.circular())
    .pause(1 second)
    .exec(login)
    .exec(getProfile())
    .pause(1 second)
    .exec(provisionMessages)
    .pause(1 second)
    .exec(provisionEvents)
    .pause(1 second)
    .exec(provisionContacts)
    .pause(1 second)
    .exec(logout)
    .pause(1 second)
    .during(ScenarioDuration) {
      randomSwitch(
        33.3 -> exec(CalendarMixScenari.generate(eventUUIDFeeder)),
        33.3 -> exec(SendEmailScenari.generate()),
        33.3 -> exec(OpenContactScenari.generate())
      ).pause(7500 milliseconds, 15 seconds)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
