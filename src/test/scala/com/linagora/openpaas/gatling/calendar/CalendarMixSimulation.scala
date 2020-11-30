package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.scenari._
import com.linagora.openpaas.gatling.core.LoginSteps
import com.linagora.openpaas.gatling.core.WebSocketSteps.closeWsConnection
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

class CalendarMixSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val eventUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))
  val scn: ScenarioBuilder = scenario("CalendarMixScenario")
    .feed(userFeeder)
    .exec(CalendarSteps.openCalendarSPA())
    .during(ScenarioDuration) {
      exec(CalendarMixScenari.generate(eventUuidFeeder))
        .pause(RandomHumanActionDelay.humanActionDelay())
        .exec(closeWsConnection)
        .exec(LoginSteps.logout)
        .pause(5 seconds, 10 seconds)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
