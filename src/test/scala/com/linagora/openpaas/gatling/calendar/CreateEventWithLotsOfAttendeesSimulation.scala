package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.calendar.scenari._
import com.linagora.openpaas.gatling.utils.RandomNumber
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

class CreateEventWithLotsOfAttendeesSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val rampUserCount: Int = if (System.getProperty("rampUserCount") != null) Integer.parseInt(System.getProperty("rampUserCount")) else 20
  val rampUserDuration: Int = if (System.getProperty("rampUserDuration") != null) Integer.parseInt(System.getProperty("rampUserDuration")) else 1
  val eventUUIDFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))
  val scn: ScenarioBuilder = scenario("CreateEventWithAttendeesScenario")
    .feed(userFeeder)
    .exec(CalendarSteps.openCalendarSPA())
    .exec(CreateEventWithAttendeesScenari.generate(eventUUIDFeeder, RandomNumber.between(100, 200)))

  setUp(scn.inject(rampUsers(rampUserCount) during(rampUserDuration))).protocols(httpProtocol)
}
