package com.linagora.openpaas.gatling.calendar.utils

import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString

object AttendeeUtils {
  def generateRandomAttendeesInICalFormat(numberOfAttendees: Int = 100): String =
    (1 to numberOfAttendees).map(index => constructAttendee(index)).mkString(",\n")

  private def constructAttendee(index: Int): String =
      s"""["attendee",{"partstat": "NEEDS-ACTION","rsvp": "TRUE","role": "REQ-PARTICIPANT","cutype": "INDIVIDUAL"},"cal-address","mailto:attendee-$index-$randomUuidString@fake.org"]""".stripMargin
}
