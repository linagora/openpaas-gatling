package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

class ListCalendarsSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val scn: ScenarioBuilder = scenario("ListCalendarsSimulation")
    .feed(userFeeder)
    .exec(CalendarSteps.openCalendarSPA())

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
