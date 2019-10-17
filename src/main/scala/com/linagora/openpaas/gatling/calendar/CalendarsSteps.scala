package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString


object CalendarsSteps {
  def createCalendar(): HttpRequestBuilder = {
    var calId = randomUuidString

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
    val eventUuid = randomUuidString

    exec(_.set(SessionKeys.EventUuid, eventUuid))

    withAuth(http("createEventOnDefaultCalendar")
      .put(s"/dav/api/calendars/$${$UserId}/$${$UserId}/$eventUuid.ics"))
      .body(StringBody(s"""
[
  "vcalendar",
  [],
  [
    [
      "vevent",
      [
        ["uid",{},"text","$eventUuid"],
        ["dtstart",{"tzid": "Europe/Berlin"},"date-time","2018-05-22T14:00:00"],
        ["dtend",{"tzid": "Europe/Berlin"},"date-time","2018-05-22T15:00:00"],
        ["summary",{},"text",""],
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
    withAuth(http("listCalendars")
      .get(s"/dav/api/calendars/$${$UserId}.json"))
      .check(status in(200, 304))
}
