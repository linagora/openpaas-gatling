package com.linagora.openpaas.gatling.addressbook.scenari

import com.linagora.openpaas.gatling.addressbook.ContactSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

object OpenContactInDefaultAddressBookScenari {
  def generate(contactUuidFeeder: Iterator[Map[String, String]]): ScenarioBuilder = {
    scenario("OpenContactInDefaultAddressBookScenari")
      .feed(contactUuidFeeder)
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(ContactSteps.createContactInDefaultAddressBook())
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(ContactSteps.listContactsInDefaultAddressBook())
      .pause(RandomHumanActionDelay.humanActionDelay())
      .exec(ContactSteps.getOneContactInDefaultAddressBook())
  }
}
