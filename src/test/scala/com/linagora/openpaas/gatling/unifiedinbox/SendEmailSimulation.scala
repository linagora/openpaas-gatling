package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.unifiedinbox.scenari.SendEmailScenari
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class SendEmailSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS send email")
    .feed(feeder.circular())
    .exec(login)
    .exec(getProfile())
    .pause(1 second)
    .exec(provisionMessages)
    .pause(5 second)
    .exec(logout)
    .pause(1 second)
    .exec(SendEmailScenari.generate())

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
