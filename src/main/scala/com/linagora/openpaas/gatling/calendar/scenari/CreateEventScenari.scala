package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.Configuration.ScenarioDuration
import com.linagora.openpaas.gatling.calendar.{CalendarSteps, EventSteps}
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import scala.concurrent.duration.DurationInt

object CreateEventScenari {
  def generate(eventUUIDFeeder: Iterator[Map[String, String]]): ScenarioBuilder =
    scenario("CreateEventScenari")
      .feed(eventUUIDFeeder)
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .during(ScenarioDuration) {
        exec(EventSteps.createEventInDefaultCalendar())
          .pause(1 second)
      }
}
