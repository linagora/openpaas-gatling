package com.linagora.openpaas.gatling.calendar.utils

import com.linagora.openpaas.gatling.Configuration.SabreBaseUrl

object CalendarUtils {
  def getCalendarIdFromCalendarLink(calendarLink: String): String =
    calendarLink.substring(calendarLink.lastIndexOf('/')).dropRight(".json".length)

  def getCalDAVBaseUrl(): String =
    if (SabreBaseUrl.isEmpty) "/dav/api" else SabreBaseUrl
}
