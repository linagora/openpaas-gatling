package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

object ViewAndDeleteCalendarScenari {
  def generate(): ScenarioBuilder =
    scenario("ViewAndDeleteCalendarScenari")
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(CalendarSteps.createCalendar())
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(CalendarSteps.getSecondCalendar())
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(session => CalendarSteps.setCalendarIdFromCalendarLinkInSession(session))
      .exec(CalendarSteps.getCalendarByCalendarIdInSession())
      .exec(CalendarSteps.getCalendarConfiguration())
      .exec(CalendarSteps.deleteCalendar())
}
