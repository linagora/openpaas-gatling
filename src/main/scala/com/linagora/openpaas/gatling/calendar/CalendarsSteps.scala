package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import com.linagora.openpaas.gatling.calendar.CalendarTemplateRequestsList._
import io.gatling.core.structure.ChainBuilder
import scala.concurrent.duration.DurationInt


object CalendarsSteps {
  def createCalendar(): HttpRequestBuilder = {
    val calId = randomUuidString

    withAuth(http("createCalendar")
      .post(s"/dav/api/calendars/$${$UserId}"))
      .body(StringBody(s"""
        {
        "id":"$calId",
        "dav:name":"Test",
        "apple:color":"#01ea18",
        "caldav:description":""
        }
        """))
      .check(status is 201)
  }

  def createEventOnDefaultCalendar(): HttpRequestBuilder = {
    http("createEventOnDefaultCalendar")
      .put(s"/dav/api/calendars/$${$UserId}/$${$UserId}/$${$EventUuid}.ics")
      .body(StringBody(s"""
        [
          "vcalendar",
          [],
          [
            [
              "vevent",
              [
                ["uid",{},"text","$${$EventUuid}"],
                ["dtstart",{"tzid": "Europe/Berlin"},"date-time","2019-10-22T14:00:00"],
                ["dtend",{"tzid": "Europe/Berlin"},"date-time","2019-10-22T15:00:00"],
                ["summary",{},"text","event-$${$EventUuid}"],
                ["organizer",{"cn": "$UsernameSessionParam"},"cal-address","mailto:$UsernameSessionParam}"],
                ["attendee",{"partstat": "ACCEPTED","rsvp": "FALSE","role": "CHAIR","cutype": "INDIVIDUAL"},"cal-address","mailto:$${$UsernameSessionParam}"]
              ],
              []
            ]
          ]
        ]
        """))
      .check(status in (201, 204))
  }

  def listCalendarsForUser(): HttpRequestBuilder =
    withAuth(http
    ("listCalendars")
      .get(s"/dav/api/calendars/$${$UserId}.json"))
      .check(status in(200, 304))

  def listUsableCalendarsForUser(): HttpRequestBuilder =
    http("listSearchableCalendars")
      .get(s"/dav/api/calendars/$${$UserId}.json?personal=true&sharedDelegationStatus=accepted&sharedPublicSubscription=true&withRights=true")
      .check(status in(200, 304))
      .check(jsonPath("$._embedded['dav:calendar'][*]._links.self.href")
        .findAll
        .saveAs("calendarLinks"))

  def listEvents(): HttpRequestBuilder =
    http("listEvents")
      .httpRequest("REPORT", s"/dav/api$${$CalendarLink}")
      .body(StringBody("""{"match":{"start":"20190929T000000","end":"20191112T000000"}}"""))
      .check(status is 200)

  def getDefaultCalendar(): HttpRequestBuilder =
    http("getDefaultCalendar")
      .get(s"/dav/api/calendars/$${$UserId}/events.json?withRights=true")
      .check(status is 200)

  def searchEvents(): HttpRequestBuilder =
    http("searchEvents")
      .get(s"/calendar/api$${$CalendarLink}/events.json?limit=30&offset=0&query=event")
      .check(status in(200, 304))

  def getCalendarConfiguration: HttpRequestBuilder =
    http("getCalendarConfiguration")
      .post("/api/configurations?scope=user")
      .body(StringBody("""[{"name":"linagora.esn.calendar","keys":["workingDays","hideDeclinedEvents"]}]"""))
      .check(status in(200, 304))

  def loadTemplatesForRedirectingToCalendarPageAfterLogin: ChainBuilder =
    group("Load templates when redirecting to calendar page after login") {
      repeat(redirectToCalendarPageAfterLogin.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = redirectToCalendarPageAfterLogin(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in(200, 304)))
      }
    }

  def loadSearchResultPageTemplates: ChainBuilder =
    group("Load search result page templates") {
      repeat(searchResultPageTemplatesRequests.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = searchResultPageTemplatesRequests(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in(200, 304)))
      }
    }

  def loadOpeningEventTemplates: ChainBuilder =
    group("Load opening event template") {
      repeat(openingEventTemplateRequests.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = openingEventTemplateRequests(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in(200, 304)))
      }
    }

  def provisionEvents: ChainBuilder = {
    val eventUuidFeeder = Iterator.continually(Map("eventUuid" -> randomUuidString))

    feed(eventUuidFeeder)
      .exec(createEventOnDefaultCalendar())
      .pause(1 second)
  }
}
