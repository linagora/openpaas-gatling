package com.linagora.openpaas.gatling.calendar

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.linagora.openpaas.gatling.Configuration.{EventCount, SabreBaseUrl}
import com.linagora.openpaas.gatling.calendar.CalendarConstants.ESNToken
import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.calendar.utils.AttendeeUtils.generateRandomAttendeesInICalFormat
import com.linagora.openpaas.gatling.calendar.utils.CalendarUtils.getCalDAVBaseUrl
import com.linagora.openpaas.gatling.calendar.utils.EventUtils
import com.linagora.openpaas.gatling.core.{LoginSteps, TokenSteps}
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt
import scala.util.matching.Regex

object EventSteps {
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYYMMdd")

  def createEventInDefaultCalendar(): ChainBuilder = {
    withAuth(http("createEvent")
      .put(s"${getCalDAVBaseUrl()}/calendars/$${$UserId}/$${$UserId}/$${$EventUuid}.ics")
      .header("Accept", "application/json, text/plain, */*")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody(session => {
        val (startString, endString): (String, String) = EventUtils.getRandomStartAndEndDateString()

        s"""[
          "vcalendar",
          [],
          [
            [
              "vevent",
              [
                ["uid",{},"text","$${$EventUuid}"],
                ["dtstart",{"tzid": "Europe/Berlin"},"date-time","$startString"],
                ["dtend",{"tzid": "Europe/Berlin"},"date-time","$endString"],
                ["summary",{},"text","event-$${$EventUuid}"],
                ["organizer",{"cn": "$${$UsernameSessionParam}"},"cal-address","mailto:$${$UsernameSessionParam}"],
                ["attendee",{"partstat": "ACCEPTED","rsvp": "FALSE","role": "CHAIR","cutype": "INDIVIDUAL"},"cal-address","mailto:$${$UsernameSessionParam}"]
              ],
              []
            ]
          ]
        ]
        """
      }))
      .check(status in (201, 204)))
  }

  def createEventInDefaultCalendarWithAttendees(numberOfAttendees: Int): ChainBuilder= {
    val attendeesInIcalFormat: String = generateRandomAttendeesInICalFormat(numberOfAttendees)

    withAuth(http("createEventWithAttendees")
      .put(s"${getCalDAVBaseUrl()}//calendars/$${$UserId}/$${$UserId}/$${$EventUuid}.ics")
      .header("Accept", "application/json, application/calendar+json, text/plain, */*")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody(session => {
        val (startString, endString): (String, String) = EventUtils.getRandomStartAndEndDateString()

        s"""[
          "vcalendar",
          [],
          [
            [
              "vevent",
              [
                ["uid",{},"text","$${$EventUuid}"],
                ["transp",{},"text","OPAQUE"],
                ["dtstart",{"tzid": "Europe/Berlin"},"date-time","$startString"],
                ["dtend",{"tzid": "Europe/Berlin"},"date-time","$endString"],
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
        """
      }))
      .check(status in (201, 204)))
  }

  def listEvents(start: LocalDate = LocalDate.now, end: LocalDate = LocalDate.now.plusWeeks(1)): ChainBuilder = {
    withAuth(listEventsRequest(start, end))
  }

  private def listEventsRequest(start: LocalDate, end: LocalDate) = {
    http("listEvents")
      .httpRequest("REPORT", s"${getCalDAVBaseUrl()}$${$CalendarLink}")
      .header("Accept", "application/json, text/plain, */*")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody(s"""{"match":{"start":"${dateTimeFormatter.format(start)}T000000","end":"${dateTimeFormatter.format(end)}T000000"}}"""))
      .check(status is 200)
  }

  def listEventsAndGetFirstEvent(start: LocalDate = LocalDate.now, end: LocalDate = LocalDate.now.plusWeeks(1)): ChainBuilder = {

    withAuth(listEventsRequest(start, end)
      .check(jsonPath("$._embedded['dav:item']").count.gt(0))
      .check(jsonPath("$._embedded['dav:item'][0]").exists)
      .check(jsonPath("$._embedded['dav:item'][0]._links.self.href").saveAs("eventLink"))
      .check(jsonPath("$._embedded['dav:item'][0].data").saveAs("eventContent")))
  }

  def updateEvent(): ChainBuilder = {
    withAuth(http("updateEvent")
      .put(s"${getCalDAVBaseUrl()}$${$EventLink}")
      .header("Accept", "application/json, text/plain, */*")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody(s"$${$NewEventContent}"))
      .check(status is 204))
  }

  def updateEventInSession(session: Session): Session = {
    val eventLink: String = session(EventLink).as[String]
    val eventContent: String = session(EventContent).as[String]
    val regex: Regex = raw"""\["summary"\,\{\}\,"text"\,"(.*?)"\]""".r
    val newEventContent: String = regex.replaceFirstIn(eventContent, s"""["summary",{},"text","[UPDATED] $eventLink - $randomUuidString"]""")

    session.set(NewEventContent, newEventContent)
  }

  def deleteEvent(): ChainBuilder = {
    withAuth(http("deleteEvent")
      .delete(s"${getCalDAVBaseUrl()}$${$EventLink}")
      .header(ESNToken, s"$${$Token}")
      .check(status is 204))
  }

  def searchEvents(): ChainBuilder =
    withAuth(http("searchEvents")
      .get(s"/calendar/api$${$CalendarLink}/events.json?limit=30&offset=0&query=event")
      .check(status in(200, 304)))

  def provisionEvents(): ChainBuilder = {
    val eventUuidFeeder = Iterator.continually(Map("eventUuid" -> randomUuidString))

    group("provisionEvents") {
      exec(session => session.set("eventCountBeforeRenewToken", 5))
        .repeat(EventCount) {
          feed(eventUuidFeeder)
            .doIfOrElse(session => session("eventCountBeforeRenewToken").as[Int] == 0)
              {
                exec(session => session.set("eventCountBeforeRenewToken", 5))
                  .exec(TokenSteps.retrieveAuthenticationToken)
              }
              {
                exec(session => {
                  val currentEventCountBeforeRenewToken: Int = session("eventCountBeforeRenewToken").as[Int]

                  session.set("eventCountBeforeRenewToken", currentEventCountBeforeRenewToken - 1)
                })
              }
            .exec(createEventInDefaultCalendar())
            .pause(1 second)
        }
    }
  }
}
