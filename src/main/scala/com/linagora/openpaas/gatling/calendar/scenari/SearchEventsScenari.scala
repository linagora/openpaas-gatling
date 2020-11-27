package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.calendar.{CalendarSteps, EventSteps}
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import scala.concurrent.duration.DurationInt

object SearchEventsScenari {
  def generate(): ChainBuilder =
    pause(RandomHumanActionDelay.humanActionDelay() second)
    .exec(CalendarSteps.listUsableCalendarsForUser())
    .group("listEventsFromUsableCalendars") {
      foreach(s"$${$CalendarLinks}", s"$${$CalendarLink}") {
        exec(EventSteps.listEvents())
      }
    }
    .pause(RandomHumanActionDelay.humanActionDelay() second)
    .group("searchEventsFromUsableCalendars") {
      foreach(s"$${$CalendarLinks}", s"$${$CalendarLink}") {
        exec(session => {
          val calendarFullLink = session(s"${CalendarLink}").as[String]
          val calendarLink = calendarFullLink.dropRight(".json".length) // remove ".json" extension
          session.set(s"${CalendarLink}", calendarLink)
        })
          .exec(EventSteps.searchEvents())
      }
    }
}
