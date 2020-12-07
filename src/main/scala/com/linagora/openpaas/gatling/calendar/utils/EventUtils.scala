package com.linagora.openpaas.gatling.calendar.utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime}

import com.linagora.openpaas.gatling.utils.RandomNumber

object EventUtils {
  def getRandomStartAndEndDateString(): (String, String) = {
    val start: LocalDateTime = LocalDateTime.now
      .minusWeeks(RandomNumber.between(0, 1))
      .minusDays(RandomNumber.between(0, 2))
      .minusHours(RandomNumber.between(0, 5))
    val end: LocalDateTime = start.plusMinutes(RandomNumber.between(30, 180))
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")

    (dateTimeFormatter.format(start), dateTimeFormatter.format(end))
  }
}
