package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.AddressBooksSteps._
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactScenari
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile

import scala.concurrent.duration.DurationInt

class OpenContactSimulation extends Simulation{
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS opening a contact")
    .feed(feeder.circular)
    .exec(login)
    .exec(getProfile())
    .exec(provisionContacts)
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(OpenContactScenari.generate())
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
