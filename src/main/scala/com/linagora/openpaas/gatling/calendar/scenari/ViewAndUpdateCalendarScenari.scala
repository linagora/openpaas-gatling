package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

object ViewAndUpdateCalendarScenari {
  def generate(): ScenarioBuilder =
    scenario("ViewAndUpdateCalendarScenari")
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(CalendarSteps.getCalendarConfiguration())
      .exec(CalendarSteps.getDefaultCalendar())
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(CalendarSteps.createCalendar())
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(session => CalendarSteps.setCalendarIdFromCalendarLinkInSession(session))
      .exec(session => CalendarSteps.updateCalendarInSession(session))
      .exec(CalendarSteps.updateCalendar())
}
