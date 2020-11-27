package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.EventSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import scala.concurrent.duration.DurationInt

object CreateEventScenari {
  def generate(eventUuidFeeder: Iterator[Map[String, String]]): ScenarioBuilder =
    scenario("CreateEventScenari")
      .feed(eventUuidFeeder)
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(EventSteps.createEventInDefaultCalendar())
}
