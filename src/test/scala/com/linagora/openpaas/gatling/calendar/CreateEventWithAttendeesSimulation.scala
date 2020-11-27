package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.scenari._
import com.linagora.openpaas.gatling.utils.RandomNumber
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

class CreateEventWithAttendeesSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val eventUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))
  val scn: ScenarioBuilder = scenario("CreateEventWithAttendeesScenario")
    .feed(userFeeder)
    .exec(CalendarSteps.openCalendarSPA())
    .exec(CreateEventWithAttendeesScenari.generate(eventUuidFeeder, RandomNumber.between(1, 10), false))

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
