package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.{CalendarSteps, EventSteps}
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

object ViewEventDetailsScenari {
  def generate(): ScenarioBuilder =
    scenario("ViewEventDetailsScenari")
      .exec(CalendarSteps.listUsableCalendarsForUser())
      .exec(EventSteps.listEvents())
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(CalendarSteps.listUsableCalendarsForUser()) // When opening the event dialog, this is the only request that is sent if there are no attendees
}
