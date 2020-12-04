package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import scala.concurrent.duration.DurationInt

object ViewAndDeleteCalendarScenari {
  def generate(): ScenarioBuilder =
    scenario("ViewAndDeleteCalendarScenari")
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(CalendarSteps.createCalendar())
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(CalendarSteps.getSecondCalendar())
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(session => CalendarSteps.setCalendarIdFromCalendarLinkInSession(session))
      .exec(CalendarSteps.getCalendarByCalendarIdInSession())
      .exec(CalendarSteps.getCalendarConfiguration())
      .exec(CalendarSteps.deleteCalendar())
}
