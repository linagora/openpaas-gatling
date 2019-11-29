package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.calendar.CalendarsSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.TokenSteps.retrieveAuthenticationToken
import com.linagora.openpaas.gatling.core.WebSocketSteps._
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay._

import io.gatling.core.Predef._
import scala.concurrent.duration.DurationInt

object SearchEventsScenari {
  def generate() =
    exec(loadLoginTemplates)
      .exec(login)
      .exec(retrieveAuthenticationToken)
      .exec(getSocketId)
      .exec(registerSocketNamespaces)
      .exec(openWsConnection())
      .exec(loadTemplatesForRedirectingToCalendarPageAfterLogin)
      .exec(getDomain)
      .exec(getLogoForDomain)
      .exec(getCalendarConfiguration)
      .exec(getDefaultCalendar)
      .pause(humanActionDelay() second)
      .exec(listUsableCalendarsForUser())
      .group("List events from usable calendars") {
        foreach("${calendarLinks}", s"${CalendarLink}") {
          exec(listEvents())
        }
      }
      .pause(humanActionDelay() second)
      .exec(loadSearchResultPageTemplates)
      .exec(listUsableCalendarsForUser())
      .pause(humanActionDelay() second)
      .group("Search events from usable calendars") {
        foreach("${calendarLinks}", s"${CalendarLink}") {
          exec(session => {
            val calendarFullLink = session(s"${CalendarLink}").as[String]
            val calendarLink = calendarFullLink.dropRight(".json".length) // remove ".json" extension
            session.set(s"${CalendarLink}", calendarLink)
          })
            .exec(searchEvents())
        }
      }
      .pause(humanActionDelay() second)
      .exec(loadOpeningEventTemplates)
      .exec(listUsableCalendarsForUser())
      .exec(closeWsConnection)
      .exec(logout)
}
