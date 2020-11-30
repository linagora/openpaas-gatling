package com.linagora.openpaas.gatling.addressbook.provisioning

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.scenari.provisioning._
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder

class ProvisionContactsSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue

  setUp(ProvisionContactsScenari.generate(userFeeder)
    .inject(rampUsers(UserCount) during(InjectDuration)))
    .protocols(HttpProtocol)
}
