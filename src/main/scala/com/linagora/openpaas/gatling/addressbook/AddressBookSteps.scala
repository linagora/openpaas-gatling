package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.Configuration.{ContactsSpaPath, LoadStaticAssets}
import com.linagora.openpaas.gatling.addressbook.SessionKeys.AddressBookLinks
import com.linagora.openpaas.gatling.core.{DomainSteps, LoginSteps, StaticAssetsSteps, TokenSteps, UserSteps, WebSocketSteps}
import com.linagora.openpaas.gatling.provisionning.Authentication

import scala.concurrent.duration.DurationInt

object AddressBookSteps {
  def openContactsSpa(): ChainBuilder = {
    group("openContactsSPA") {
      doIfEquals(LoadStaticAssets, true) {
        exec(LoginSteps.loadLoginTemplates)
      }
        .exec(LoginSteps.login(ContactsSpaPath))
        .exec(StaticAssetsSteps.loadIndexHtmlAndMainJs(ContactsSpaPath))
        .doIfEquals(LoadStaticAssets, true) {
          exec(StaticAssetsSteps.loadStaticAssets(ContactsStaticAssets.OpeningContactsAssets))
        }
        .exec(UserSteps.getProfile())
        .exec(WebSocketSteps.openWsConnection())
        .exec(DomainSteps.getDomain)
        .exec(DomainSteps.getThemeForDomain())
        .exec(DomainSteps.getLogoForDomain)
        .exec(getUserGroupMembershipPrincipals)
        .exec(getDomainAddressBooks)
        .exec(getUserAddressBooks)
        .exec(ContactSteps.listContactsInDefaultAddressBook())
    }
  }

  def getUserGroupMembershipPrincipals: ChainBuilder =
    Authentication.withAuth(http("getUserGroupMembershipPrincipals")
      .httpRequest("PROPFIND", s"/dav/api/principals/users/$${$UserId}")
      .check(status in(200, 304)))

  def getUserAddressBooks: ChainBuilder =
    Authentication.withAuth(http("getUserAddressBooks")
      .get(s"/dav/api/addressbooks/$${$UserId}.json?contactsCount=true&inviteStatus=2&personal=true&shared=true&subscribed=true")
      .check(status in(200, 304))
      .check(jsonPath("$._embedded['dav:addressbook'][*]._links.self.href")
        .findAll
        .saveAs(AddressBookLinks)))

  def getDomainAddressBooks: ChainBuilder =
    Authentication.withAuth(http("getDomainAddressBooks")
      .get(s"/dav/api/addressbooks/$${$DomainId}.json?contactsCount=true&inviteStatus=2&personal=true&shared=true&subscribed=true")
      .check(status in(200, 304))
      .check(jsonPath("$._embedded['dav:addressbook']")))

  def getCollectedAddressBookProperty: ChainBuilder =
    Authentication.withAuth(http("getCollectedAddressBookProperty")
      .httpRequest("PROPFIND", s"/dav/api/addressbooks/$${$UserId}/collected.json")
      .check(status in(200, 304)))

  def idle(): ChainBuilder =
    pause(30 seconds, 60 seconds)
}