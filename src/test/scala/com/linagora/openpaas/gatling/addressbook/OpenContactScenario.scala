package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.AddressBooksSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.TokenSteps.retrieveAuthenticationToken
import com.linagora.openpaas.gatling.core.WebSocketSteps._
import com.linagora.openpaas.gatling.provisionning.ProvisioningSteps.provision
import com.linagora.openpaas.gatling.provisionning.RandomFeeder
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class OpenContactScenario extends Simulation{
  val feeder = new RandomFeeder(UserCount)

  val scn = scenario("Testing OpenPaaS opening a contact")
    .exec(createGatlingTestDomainIfNotExist)
    .pause(1 second)
    .feed(feeder.asFeeder())
    .pause(1 second)
    .exec(provision())
    .pause(1 second)
    .exec(provisionContacts)
    .pause(1 second)
    .exec(loadLoginTemplates)
    .pause(1 second)
    .exec(login())
    .pause(1 second)
    .exec(retrieveAuthenticationToken)
    .exec(getSocketId)
    .exec(registerSocketNamespaces)
    .exec(openConnection())
    .exec(loadTemplatesForRedirectingToContactPageAfterLogin)
    .exec(getDomain)
    .exec(getLogoForDomain)
    .exec(getUserGroupMembershipPrincipals)
    .exec(getUserAddressBooks)
    .exec(getDomainAddressBooks)
    .exec(listContactsFromUserAddressBooks)
    .pause(1 second)
    .exec(getCollectedAddressBookProperty)
    .exec(listContactsFromCollectedAddressBook)
    .pause(1 second)
    .exec(loadTemplatesForOpeningContact)
    .exec(getOneCreatedContactInCollectedAddressBook)
    .pause(1 second)
    .exec(logout)


  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
