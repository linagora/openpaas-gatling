package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.SessionKeys.ContactUuid
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInDefaultAddressBookScenari
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder

class OpenContactInDefaultAddressBookSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val contactUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map(ContactUuid -> randomUuidString))

  val scn = scenario("OpenContactInDefaultAddressBookScenario")
    .feed(userFeeder)
    .exec(AddressBookSteps.openContactsSpa())
    .exec(OpenContactInDefaultAddressBookScenari.generate(contactUuidFeeder))

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
