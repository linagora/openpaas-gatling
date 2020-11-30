package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.utils.{RandomHumanActionDelay, RandomNumber}
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

object CalendarMixScenari {
  def generate(eventUuidFeeder: Iterator[Map[String, String]]): ScenarioBuilder = {
    scenario("CalendarMixScenari")
      .pause(RandomHumanActionDelay.humanActionDelay())
      .randomSwitch(
        40.0 -> exec(ViewEventDetailsScenari.generate()),
        20.0 -> exec(CreateEventWithAttendeesScenari.generate(eventUuidFeeder, RandomNumber.between(1, 10))),
        15.0 -> exec(ViewAndUpdateEventScenari.generate()),
        10.0 -> exec(CreateEventScenari.generate(eventUuidFeeder)),
        5.0 -> exec(ViewAndDeleteEventScenari.generate()),
        5.0 -> exec(ViewAndUpdateCalendarScenari.generate()),
        3.0 -> exec(CreateCalendarScenari.generate()),
        2.0 -> exec(ViewAndDeleteCalendarScenari.generate())
      )
  }
}
