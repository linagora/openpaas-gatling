package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendarAndContact.scenari.CalendarAndContactsScenari
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

class CalendarAndContactsWithRefreshTokenSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val eventUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))
  val contactUuidFeeder = Iterator.continually(Map("contactUuid" -> randomUuidString))
  val scn: ScenarioBuilder = CalendarAndContactsScenari.generate(eventUuidFeeder, contactUuidFeeder, userFeeder)

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
