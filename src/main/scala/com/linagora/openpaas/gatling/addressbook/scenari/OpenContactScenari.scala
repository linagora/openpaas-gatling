package com.linagora.openpaas.gatling.addressbook.scenari

import com.linagora.openpaas.gatling.addressbook.AddressBooksSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.TokenSteps.retrieveAuthenticationToken
import com.linagora.openpaas.gatling.core.WebSocketSteps._
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

object OpenContactScenari {

  def generate() =
    exec(loadLoginTemplates)
      .pause(1 second)
      .exec(login())
      .pause(1 second)
      .exec(retrieveAuthenticationToken)
      .exec(getSocketId)
      .exec(registerSocketNamespaces)
      .exec(openWsConnection())
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
      .exec(closeWsConnection)
      .exec(logout)
}
