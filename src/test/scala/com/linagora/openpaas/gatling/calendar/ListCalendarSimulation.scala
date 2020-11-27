package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.CalendarsSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.LoginSteps.login
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class ListCalendarSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS calendar listing")
    .feed(feeder.circular())
    .pause(1 second)
    .exec(login)
    .pause(1 second)
    .exec(getProfile())
    .pause(1 second)
    .exec(createCalendar())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(listCalendarsForUser())
        .pause(1 second)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
