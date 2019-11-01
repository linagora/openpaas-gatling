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
    .pause(1 second)
    .exec(login())
    .pause(1 second)
    .exec(provisionEvents)
    .pause(1 second)
    .exec(loadTemplatesForRedirectingToCalendarPageAfterLogin)
    .pause(1)
    .exec(getDomain)
    .pause(1 second)
    .exec(getLogoForDomain)
    .pause(1 second)
    .exec(getCalendarConfiguration)
    .pause(1)
    .exec(getDefaultCalendar)
    .pause(1 second)
    .exec(listUsableCalendarsForUser())
    .pause(1 second)
    .group("List events from usable calendars") {
      foreach("${calendarLinks}", s"${CalendarLink}") {
        exec(listEvents()).pause(1 second)
      }
    }
    .exec(loadSearchResultPageTemplates)
    .pause(1 second)
    .exec(listUsableCalendarsForUser())
    .pause(1 second)
    .group("Search events from usable calendars") {
      foreach("${calendarLinks}", s"${CalendarLink}") {
        exec(session => {
          val calendarFullLink = session(s"${CalendarLink}").as[String]
          val calendarLink = calendarFullLink.dropRight(5) // remove ".json" extension
          session.set(s"${CalendarLink}", calendarLink)
        })
          .exec(searchEvents()).pause(1 second)
      }
    }
    .exec(loadOpeningEventTemplates)
    .pause(1 second)
    .exec(listUsableCalendarsForUser())
    .pause(1 second)

    setUp(
      scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
