package com.linagora.openpaas.gatling.addressbook.scenari

import com.linagora.openpaas.gatling.addressbook.ContactSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

object OpenContactInDefaultAddressBookScenari {
  def generate(): ScenarioBuilder =
    scenario("OpenContactInDefaultAddressBookScenari")
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(ContactSteps.createContactInDefaultAddressBook())
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(ContactSteps.listContactsInDefaultAddressBook())
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(ContactSteps.getOneContactInDefaultAddressBook())
}
