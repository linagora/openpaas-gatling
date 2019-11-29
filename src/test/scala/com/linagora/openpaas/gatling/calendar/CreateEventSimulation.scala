package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.CalendarsSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import com.linagora.openpaas.gatling.core.LoginSteps._
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class CreateEventSimulation extends Simulation {
  private val feeder = csv("users.csv")
  val eventUuidFeeder = Iterator.continually(Map("eventUuid" -> randomUuidString))

  val scn = scenario("Testing OpenPaaS calendar listing")
    .feed(feeder.circular)
    .exec(login)
    .exec(getProfile())
    .during(ScenarioDuration) {
      feed(eventUuidFeeder)
      .exec(createEventOnDefaultCalendar())
        .pause(1 second)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
