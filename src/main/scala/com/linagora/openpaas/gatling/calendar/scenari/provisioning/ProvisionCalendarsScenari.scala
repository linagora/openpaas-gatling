package com.linagora.openpaas.gatling.calendar.scenari.provisioning

import com.linagora.openpaas.gatling.Configuration.CalendarSpaPath
import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.core.{LoginSteps, TokenSteps, UserSteps}
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

object ProvisionCalendarsScenari {
  def generate(userFeeder: SourceFeederBuilder[String]): ScenarioBuilder =
    scenario(s"ProvisionCalendarsScenari")
      .feed(userFeeder)
      .pause(5 seconds)
      .exec(LoginSteps.login(CalendarSpaPath))
      .exec(UserSteps.getProfile())
      .exec(CalendarSteps.provisionCalendars())
}
