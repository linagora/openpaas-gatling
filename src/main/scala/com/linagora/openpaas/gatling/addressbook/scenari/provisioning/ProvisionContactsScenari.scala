package com.linagora.openpaas.gatling.addressbook.scenari.provisioning

import com.linagora.openpaas.gatling.addressbook.ContactSteps
import com.linagora.openpaas.gatling.core.{LoginSteps, UserSteps}
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

object ProvisionContactsScenari {
  def generate(userFeeder: SourceFeederBuilder[String]): ScenarioBuilder =
    scenario(s"ProvisionContactsScenari")
      .feed(userFeeder)
      .exec(LoginSteps.login())
      .exec(UserSteps.getProfile())
      .exec(ContactSteps.provisionContacts())
}
