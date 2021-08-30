package com.linagora.openpaas.gatling.calendar.scenari

import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

object CreateCalendarScenari {
  def generate(): ScenarioBuilder =
    scenario("CreateCalendarScenari")
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(CalendarSteps.getCalendarConfiguration())
      .exec(CalendarSteps.createCalendar())
}
