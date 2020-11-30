package com.linagora.openpaas.gatling.addressbook.scenari

import com.linagora.openpaas.gatling.addressbook.ContactSteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

object OpenContactInCollectedAddressBookScenari {
  def generate(): ScenarioBuilder =
    scenario("OpenContactInCollectedAddressBookScenari")
      .pause(RandomHumanActionDelay.humanActionDelay() second)
      .exec(ContactSteps.getOneCreatedContactInCollectedAddressBook())
}
