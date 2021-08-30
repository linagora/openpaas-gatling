package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.EventSteps
import com.linagora.openpaas.gatling.core.PeopleSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

object CreateEventWithAttendeesScenari {
  def generate(eventUuidFeeder: Iterator[Map[String, String]], eventAttendeeCount: Int, isEmailGroup: Boolean = false): ScenarioBuilder =
    scenario("CreateEventWithAttendeesScenari")
      .pause(RandomHumanActionDelay.humanActionDelay())
      .feed(eventUuidFeeder)
      .group("simulateAttendeeSearch") {
        if (!isEmailGroup) {
          repeat(eventAttendeeCount) {
            exec(PeopleSteps.simulatePeopleSearch())
              .pause(2 second)
          }
        } else {
          exec(PeopleSteps.simulatePeopleSearch())
        }
      }
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(EventSteps.createEventInDefaultCalendarWithAttendees(eventAttendeeCount))
}
