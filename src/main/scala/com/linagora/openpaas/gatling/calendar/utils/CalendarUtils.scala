package com.linagora.openpaas.gatling.calendar.utils

object CalendarUtils {
  def getCalendarIdFromCalendarLink(calendarLink: String): String =
    calendarLink.substring(calendarLink.lastIndexOf('/')).dropRight(".json".length)
}
