package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.unifiedinbox.scenari.InboxScenari
import io.gatling.core.Predef._

class InboxWithRefreshTokenSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = InboxScenari.platform()
    .feed(feeder.circular())

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
