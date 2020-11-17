package com.linagora.openpaas.gatling.calendar

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.linagora.openpaas.gatling.Configuration.{EventCount, SabreBaseUrl}
import com.linagora.openpaas.gatling.calendar.CalendarConstants.ESNToken
import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.calendar.utils.AttendeeUtils.generateRandomAttendeesInICalFormat
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.concurrent.duration.DurationInt
import scala.util.matching.Regex

object EventSteps {
  def createEventInDefaultCalendar(): HttpRequestBuilder = {
    http("createEvent")
      .put(s"$SabreBaseUrl/calendars/$${$UserId}/$${$UserId}/$${$EventUuid}.ics")
      .header(ESNToken, s"$${$Token}")
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

  def createEventInDefaultCalendarWithAttendees(numberOfAttendees: Int): HttpRequestBuilder= {
    val attendeesInIcalFormat: String = generateRandomAttendeesInICalFormat(numberOfAttendees)

    http("createEventWithAttendees")
      .put(s"$SabreBaseUrl/calendars/$${$UserId}/$${$UserId}/$${$EventUuid}.ics")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody(s"""
        [
          "vcalendar",
          [],
          [
            [
              "vevent",
              [
                ["uid",{},"text","$${$EventUuid}"],
                ["transp",{},"text","OPAQUE"],
                ["dtstart",{"tzid": "Europe/Berlin"},"date-time","2021-10-22T14:00:00"],
                ["dtend",{"tzid": "Europe/Berlin"},"date-time","2021-10-22T15:00:00"],
                ["summary",{},"text","event-with-attendees-3-$${$EventUuid}"],
                ["class",{},"text","PUBLIC"],
                ["organizer",{"cn": "$${$UsernameSessionParam}"},"cal-address","mailto:$${$UsernameSessionParam}"],
                ["attendee",{"partstat": "ACCEPTED","rsvp": "FALSE","role": "CHAIR","cutype": "INDIVIDUAL"},"cal-address","mailto:$${$UsernameSessionParam}"],
                $attendeesInIcalFormat
              ],
              []
            ]
          ]
        ]
        """))
      .check(status in (201, 204))
  }

  def listEvents(monthRange: Int = 1): HttpRequestBuilder = {
    val start: LocalDate = LocalDate.now.minusMonths(monthRange)
    val end: LocalDate = start.plusMonths(monthRange)
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYYMMdd")

    http("listEvents")
      .httpRequest("REPORT", s"$SabreBaseUrl$${$CalendarLink}")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody (s"""{"match":{"start":"${dateTimeFormatter.format(start)}T000000","end":"${dateTimeFormatter.format(end)}T000000"}}""") )
      .check(status is 200)
  }

  def listEventsAndGetFirstEvent(monthRange: Int = 1): HttpRequestBuilder =
    listEvents(monthRange)
      .check(jsonPath("$._embedded['dav:item']").count.gt(0))
      .check(jsonPath("$._embedded['dav:item'][0]").exists)
      .check(jsonPath("$._embedded['dav:item'][0]._links.self.href").saveAs("eventLink"))
      .check(jsonPath("$._embedded['dav:item'][0].data").saveAs("eventContent"))

  def updateEvent(): HttpRequestBuilder = {
    http("updateEvent")
      .put(s"$SabreBaseUrl$${$EventLink}")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody(s"$${$NewEventContent}"))
      .check(status is 204)
  }

  def updateEventInSession(session: Session): Session = {
    val eventLink: String = session(EventLink).as[String]
    val eventContent: String = session(EventContent).as[String]
    val regex: Regex = raw"""\["summary"\,\{\}\,"text"\,"(.*?)"\]""".r
    val newEventContent: String = regex.replaceFirstIn(eventContent, s"""["summary",{},"text","[UPDATED] $eventLink - $randomUuidString"]""")

    session.set(NewEventContent, newEventContent)
  }

  def deleteEvent(): HttpRequestBuilder = {
    http("deleteEvent")
      .delete(s"$SabreBaseUrl$${$EventLink}")
      .header(ESNToken, s"$${$Token}")
      .check(status is 204)
  }

  def searchEvents(): HttpRequestBuilder =
    http("searchEvents")
      .get(s"/calendar/api$${$CalendarLink}/events.json?limit=30&offset=0&query=event")
      .header(ESNToken, s"$${$Token}")
      .check(status in(200, 304))

  def provisionEvents(): ChainBuilder = {
    val eventUuidFeeder = Iterator.continually(Map("eventUuid" -> randomUuidString))

    group("provisionEvents") {
      repeat(EventCount) {
        feed(eventUuidFeeder)
          .exec(createEventInDefaultCalendar())
          .pause(1 second)
      }
    }
  }
}
