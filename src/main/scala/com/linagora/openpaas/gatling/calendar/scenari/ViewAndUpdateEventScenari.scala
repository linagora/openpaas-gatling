package com.linagora.openpaas.gatling.calendar.scenari

import java.time.LocalDate

import com.linagora.openpaas.gatling.calendar.{CalendarSteps, EventSteps}
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

object ViewAndUpdateEventScenari {
  def generate(eventUuidFeeder: Iterator[Map[String, String]]): ScenarioBuilder = {
    val start: LocalDate = LocalDate.now.minusMonths(1)
    val end: LocalDate = start.plusMonths(1)

    scenario("ViewAndUpdateEventScenari")
      .pause(RandomHumanActionDelay.humanActionDelay())
      .feed(eventUuidFeeder)
      .exec(EventSteps.createEventInDefaultCalendar())
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(EventSteps.listEventsAndGetFirstEvent(start, end))
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(CalendarSteps.listUsableCalendarsForUser()) // When opening the event dialog, this is the only request that is sent if there are no attendees
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(session => EventSteps.updateEventInSession(session))
      .exec(EventSteps.updateEvent())
  }
}
