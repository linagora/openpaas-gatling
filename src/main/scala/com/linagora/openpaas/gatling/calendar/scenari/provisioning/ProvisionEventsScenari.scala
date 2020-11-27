package com.linagora.openpaas.gatling.calendar.scenari.provisioning

import com.linagora.openpaas.gatling.calendar.EventSteps
import com.linagora.openpaas.gatling.core.{LoginSteps, UserSteps}
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

object ProvisionEventsScenari {
  def generate(userFeeder: SourceFeederBuilder[String]): ScenarioBuilder =
    scenario(s"CalendarProvisionEvents")
      .feed(userFeeder)
      .exec(LoginSteps.login())
      .exec(UserSteps.getProfile())
      .exec(EventSteps.provisionEvents())
}
