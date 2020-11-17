package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.{CalendarSteps, EventSteps}
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

object CreateEventWithAttendeesScenari {
  def generate(eventUUIDFeeder: Iterator[Map[String, String]], eventAttendeeCount: Int): ScenarioBuilder =
    scenario("CreateEventWithAttendeesScenari")
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .feed(eventUUIDFeeder)
      .exec(EventSteps.createEventInDefaultCalendarWithAttendees(eventAttendeeCount))
}
