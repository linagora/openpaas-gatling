package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.CalendarsSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.LoginSteps.login
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import com.linagora.openpaas.gatling.core.DomainSteps._
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.calendar.scenari.SearchEventsScenari

import scala.concurrent.duration.DurationInt

class SearchEventsSimulation extends  Simulation{
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS calendar searching")
    .pause(1 second)
    .feed(feeder.circular())
    .pause(1 second)
    .exec(login)
    .exec(getProfile())
    .pause(1 second)
    .exec(provisionEvents)
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(SearchEventsScenari.generate())
    }

    setUp(
      scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
