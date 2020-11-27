package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.scenari._
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

class ViewAndDeleteEventSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val scn: ScenarioBuilder = scenario("ViewAndDeleteEventSimulation")
    .feed(userFeeder)
    .exec(CalendarSteps.openCalendarSPA())
    .exec(ViewAndDeleteEventScenari.generate())

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
