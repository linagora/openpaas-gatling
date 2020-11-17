package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.Configuration.ScenarioDuration
import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.utils.RandomNumber
import io.gatling.core.Predef.{clock, exec, randomSwitch, scenario}
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

object CalendarMixScenari {
  def generate(eventUUIDFeeder: Iterator[Map[String, String]]): ScenarioBuilder = {
    scenario("CalendarMixScenari")
      .exec(CalendarSteps.openCalendarSPA())
      .during(ScenarioDuration) {
        randomSwitch(
          40.0 -> exec(ViewEventDetailsScenari.generate()),
          20.0 -> exec(CreateEventWithAttendeesScenari.generate(eventUUIDFeeder, RandomNumber.between(1, 10))),
          15.0 -> exec(ViewAndUpdateEventScenari.generate()),
          10.0 -> exec(CreateEventScenari.generate(eventUUIDFeeder)),
          5.0 -> exec(ViewAndDeleteEventScenari.generate()),
          5.0 -> exec(ViewAndUpdateCalendarScenari.generate()),
          3.0 -> exec(CreateCalendarScenari.generate()),
          2.0 -> exec(ViewAndDeleteCalendarScenari.generate())
        ).pause(5 seconds, 10 seconds)
      }
  }
}
