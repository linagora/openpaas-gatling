package com.linagora.openpaas.gatling.calendar.utils

import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString

object AttendeeUtils {
  def generateRandomAttendeesInICalFormat(numberOfAttendees: Int = 100): String = {
    var attendees = "";

    1 to numberOfAttendees foreach { index => {
      val attendeeEmail = s"attendee-$randomUuidString@fake.org"

      attendees = attendees.concat(
        s"""["attendee",{"partstat": "NEEDS-ACTION","rsvp": "TRUE","role": "REQ-PARTICIPANT","cutype": "INDIVIDUAL"},"cal-address","mailto:$attendeeEmail"]""".stripMargin
      )

      if (index != numberOfAttendees) attendees = attendees.concat(",\n")
    }}

    attendees;
  }
}
