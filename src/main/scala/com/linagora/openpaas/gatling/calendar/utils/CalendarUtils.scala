package com.linagora.openpaas.gatling.calendar.utils

import com.linagora.openpaas.gatling.Configuration.SabreBaseUrl

object CalendarUtils {
  def getCalendarIdFromCalendarLink(calendarLink: String): String = {
    var calendarId = calendarLink.substring(calendarLink.lastIndexOf('/')).dropRight(".json".length)

    if (calendarId.take(1) == "/") calendarId = calendarId.drop(1)

    calendarId
  }

  def getCalDAVBaseUrl(): String =
    if (SabreBaseUrl.isEmpty) "/dav/api" else SabreBaseUrl
}
