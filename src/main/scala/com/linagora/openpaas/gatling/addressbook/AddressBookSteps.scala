package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.Configuration.ContactsSpaPath
import com.linagora.openpaas.gatling.core.{DomainSteps, LoginSteps, StaticAssetsSteps, TokenSteps, UserSteps, WebSocketSteps}
import com.linagora.openpaas.gatling.provisionning.Authentication

import scala.concurrent.duration.DurationInt

object AddressBookSteps {
  def openContactsSpa(): ChainBuilder = {
    group("openContactsSPA") {
      exec(LoginSteps.loadLoginTemplates)
        .exec(LoginSteps.login())
        .exec(StaticAssetsSteps.loadIndexHtmlAndMainJs(ContactsSpaPath))
        .exec(StaticAssetsSteps.loadStaticAssets(ContactsStaticAssets.OpeningContactsAssets))
        .exec(TokenSteps.retrieveAuthenticationToken)
        .exec(UserSteps.getProfile())
        .exec(WebSocketSteps.getSocketId)
        .exec(WebSocketSteps.registerSocketNamespaces)
        .exec(WebSocketSteps.openWsConnection())
        .exec(DomainSteps.getDomain)
        .exec(DomainSteps.getThemeForDomain())
        .exec(DomainSteps.getLogoForDomain)
        .exec(getUserGroupMembershipPrincipals)
        .exec(getUserAddressBooks)
        .exec(getDomainAddressBooks)
        .exec(ContactSteps.listContactsFromUserAddressBooks())
    }
  }

  def getUserGroupMembershipPrincipals: HttpRequestBuilder =
    Authentication.withAuth(http("getUserGroupMembershipPrincipals")
      .httpRequest("PROPFIND", s"/dav/api/principals/users/$${$UserId}")
      .check(status in(200, 304)))

  def getUserAddressBooks: HttpRequestBuilder =
    Authentication.withAuth(http("getUserAddressBooks")
      .get(s"/dav/api/addressbooks/$${$UserId}.json?contactsCount=true&inviteStatus=2&personal=true&shared=true&subscribed=true")
      .check(status in(200, 304))
      .check(jsonPath("$._embedded['dav:addressbook'][*]._links.self.href")
        .findAll
        .saveAs("addressBookLinks")))

  def getDomainAddressBooks: HttpRequestBuilder =
    Authentication.withAuth(http("getDomainAddressBooks")
      .get(s"/dav/api/addressbooks/$${$DomainId}.json?contactsCount=true&inviteStatus=2&personal=true&shared=true&subscribed=true")
      .check(status in(200, 304))
      .check(jsonPath("$._embedded['dav:addressbook']")))

  def getCollectedAddressBookProperty: HttpRequestBuilder =
    Authentication.withAuth(http("getCollectedAddressBookProperty")
      .httpRequest("PROPFIND", s"/dav/api/addressbooks/$${$UserId}/collected.json")
      .check(status in(200, 304)))

  def idle(): ChainBuilder =
    pause(30 seconds, 60 seconds)
}