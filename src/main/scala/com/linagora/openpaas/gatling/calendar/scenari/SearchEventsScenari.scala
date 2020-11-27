package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.calendar.{CalendarSteps, EventSteps}
import com.linagora.openpaas.gatling.core.LoginSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

import scala.concurrent.duration.DurationInt

object SearchEventsScenari {
  def generate(): ChainBuilder =
    exec(LoginSteps.login())
    .exec(CalendarSteps.getDefaultCalendar())
    .pause(RandomHumanActionDelay.humanActionDelay() second)
    .exec(CalendarSteps.listUsableCalendarsForUser())
    .group("List events from usable calendars") {
      foreach("${calendarLinks}", s"${CalendarLink}") {
        exec(EventSteps.listEvents())
      }
    }
    .pause(RandomHumanActionDelay.humanActionDelay() second)
    .exec(CalendarSteps.listUsableCalendarsForUser())
    .pause(RandomHumanActionDelay.humanActionDelay() second)
    .group("Search events from usable calendars") {
      foreach("${calendarLinks}", s"${CalendarLink}") {
        exec(session => {
          val calendarFullLink = session(s"${CalendarLink}").as[String]
          val calendarLink = calendarFullLink.dropRight(".json".length) // remove ".json" extension
          session.set(s"${CalendarLink}", calendarLink)
        })
          .exec(EventSteps.searchEvents())
      }
    }
}
