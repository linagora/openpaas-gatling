package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.scenari.SearchEventsScenari
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

class SearchEventsSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv")
  val scn: ScenarioBuilder = scenario("SearchEventsSimulation")
    .feed(userFeeder)
    .exec(CalendarSteps.openCalendarSPA())
    .exec(SearchEventsScenari.generate())

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
