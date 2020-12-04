package com.linagora.openpaas.gatling.calendar.scenari.provisioning

import com.linagora.openpaas.gatling.calendar.EventSteps
import com.linagora.openpaas.gatling.core.{LoginSteps, TokenSteps, UserSteps}
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import scala.concurrent.duration.DurationInt

object ProvisionEventsScenari {
  def generate(userFeeder: SourceFeederBuilder[String]): ScenarioBuilder =
    scenario(s"ProvisionEventsScenari")
      .feed(userFeeder)
      .pause(5 seconds)
      .exec(LoginSteps.login())
      .exec(UserSteps.getProfile())
      .exec(TokenSteps.retrieveAuthenticationToken)
      .exec(EventSteps.provisionEvents())
}
