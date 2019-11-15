package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendar.CalendarsSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import com.linagora.openpaas.gatling.core.DomainSteps._
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.core.LoginSteps._

import scala.concurrent.duration.DurationInt

class SearchEventsScenario extends  Simulation{
  val feeder = new RandomFeeder(UserCount)
  val eventUuidFeeder = Iterator.continually(Map("eventUuid" -> randomUuidString))

  val scn = scenario("Testing OpenPaaS calendar searching")
    .exec(createGatlingTestDomainIfNotExist)
    .pause(1 second)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(loadLoginTemplates)
    .exec(login())
    .exec(provisionEvents)
    .pause(1 second)
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
    .exec(logout)

    setUp(
      scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
