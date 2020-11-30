package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.LoginSteps.{login, logout}
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.authentication.pkce.PKCESteps
import com.linagora.openpaas.gatling.unifiedinbox.scenari.InboxScenari
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class InboxWithRefreshTokenSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS mix scenarios")
    .feed(feeder.circular())
    .exec(login)
    .exec(getProfile())
    .exec(logout)
    .during(ScenarioDuration) {
        group("INBOX")(
          exec(InboxScenari.userLogin())
            .exec(repeat(20) {
              exec(PKCESteps.renewAccessToken)
                .randomSwitch(
                  10.0 -> exec(InboxScenari.generateOnceAlreadyLogged()),
                  90.0 -> exec(InboxScenari.idle())
                )
            }.exec(InboxScenari.userLogout()))
        ).pause(7500 milliseconds, 15 seconds)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
