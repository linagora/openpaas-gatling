package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.EventSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

object CreateEventScenari {
  def generate(eventUuidFeeder: Iterator[Map[String, String]]): ScenarioBuilder =
    scenario("CreateEventScenari")
      .feed(eventUuidFeeder)
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(EventSteps.createEventInDefaultCalendar())
}
