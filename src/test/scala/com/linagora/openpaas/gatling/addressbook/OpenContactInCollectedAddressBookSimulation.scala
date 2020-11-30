package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInCollectedAddressBookScenari
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder

class OpenContactInCollectedAddressBookSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val scn = scenario("OpenContactInCollectedAddressBookScenario")
    .feed(userFeeder)
    .during(ScenarioDuration) {
      exec(OpenContactInCollectedAddressBookScenari.generate())
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
