package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInDefaultAddressBookScenari
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder

class OpenContactInDefaultAddressBookSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val scn = scenario("OpenContactInDefaultAddressBookScenario")
    .feed(userFeeder)
    .exec(AddressBookSteps.openContactsSpa())
    .exec(OpenContactInDefaultAddressBookScenari.generate())

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
