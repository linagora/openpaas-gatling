package com.linagora.openpaas.gatling

import scala.concurrent.duration.DurationInt
import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder.toFeeder
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ProvisioningScenario extends Simulation {
  val scn = scenario("Testing OpenPaaS provisioning")
    .feed(toFeeder(UserCount))
    .pause(1 second, 1 minute)
    .exec(provision())
    .pause(1 second)
    .during(ScenarioDuration) {
      exec(
        withAuth(
          http("SampleQuery")
            .get("/api/user"))
          .check(status.in(200, 304)))
        .pause(1 second)
    }

  setUp(scn.inject(atOnceUsers(UserCount))).protocols(httpProtocol)
}
