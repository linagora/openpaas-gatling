package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.calendar.CalendarsSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.TokenSteps.retrieveAuthenticationToken
import com.linagora.openpaas.gatling.core.WebSocketSteps._

object SearchEventsScenari {
  def generate() =
    exec(loadLoginTemplates)
      .exec(login())
      .exec(retrieveAuthenticationToken)
      .exec(getSocketId)
      .exec(registerSocketNamespaces)
      .exec(openWsConnection())
      .exec(loadTemplatesForRedirectingToCalendarPageAfterLogin)
      .exec(getDomain)
      .exec(getLogoForDomain)
      .exec(getCalendarConfiguration)
      .exec(getDefaultCalendar)
      .exec(listUsableCalendarsForUser())
      .group("List events from usable calendars") {
        foreach("${calendarLinks}", s"${CalendarLink}") {
          exec(listEvents())
        }
      }
      .exec(loadSearchResultPageTemplates)
      .exec(listUsableCalendarsForUser())
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
      .exec(loadOpeningEventTemplates)
      .exec(listUsableCalendarsForUser())
      .exec(closeWsConnection)
      .exec(logout)
}
