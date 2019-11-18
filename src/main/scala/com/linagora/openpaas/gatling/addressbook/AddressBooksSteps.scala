package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.addressbook.AddressBookTemplateRequestList._
import com.linagora.openpaas.gatling.addressbook.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.Configuration.ContactCount

import scala.concurrent.duration.DurationInt

object AddressBooksSteps {
  def createContactOnDefaultAddressBook: HttpRequestBuilder = {
    http("create contact on default addressBook")
      .put(s"/dav/api/addressbooks/$${$UserId}/collected/$${$ContactUuid}.vcf")
      .body(StringBody(s"""
        [
          "vcard",
          [
            ["version", {}, "text", "4.0"],
            ["uid", {}, "text", "$${$ContactUuid}"],
            ["fn", {}, "text", "Dummy"],
            ["n", {}, "text", ["", "Dummy"]]
          ],
          []
        ]
        """))
      .check(status is 201)
  }

  def provisionContacts = {
    val contactUuidFeeder = Iterator.continually(Map("contactUuid" -> randomUuidString))

    group("Provision contacts") {
      repeat(ContactCount) {
        feed(contactUuidFeeder)
          .exec(withAuth(createContactOnDefaultAddressBook))
          .pause(1 second)
      }
    }
  }

  def loadTemplatesForRedirectingToContactPageAfterLogin: ChainBuilder =
    group("Load template when redirecting to contact page after login") {
      repeat(redirectToContactPageAfterLogin.length, "index" ) {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = redirectToContactPageAfterLogin(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in (200, 304)))
      }
    }

  def loadTemplatesForOpeningContact: ChainBuilder =
    group("Load templates when opening contact display page") {
      repeat(openingContactTemplateRequests.length, "index" ) {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = openingContactTemplateRequests(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in (200, 304)))
      }
    }

  def getUserGroupMembershipPrincipals: HttpRequestBuilder =
    http("get user group membership principal")
      .httpRequest("PROPFIND", s"/dav/api/principals/users/$${$UserId}")
      .check(status is 200)

  def getUserAddressBooks: HttpRequestBuilder =
    http("list address books from book home")
      .get(s"/dav/api/addressbooks/$${$UserId}.json?contactsCount=true&inviteStatus=2&personal=true&shared=true&subscribed=true")
      .check(status in(200, 304))
      .check(jsonPath("$._embedded['dav:addressbook'][*]._links.self.href")
        .findAll
        .saveAs("addressBookLinks"))

  def getDomainAddressBooks: HttpRequestBuilder =
    http("get domain address book links")
      .get(s"/dav/api/addressbooks/$${$DomainId}.json?contactsCount=true&inviteStatus=2&personal=true&shared=true&subscribed=true")
      .check(status in(200, 304))
      .check(jsonPath("$._embedded['dav:addressbook']"))

  def listContactsFromAddressBook(addressBookLink: String): HttpRequestBuilder =
    http(s"load contacts from address book $addressBookLink")
      .get(s"/dav/api$addressBookLink?limit=20&offset=0&sort=fn")
      .check(status in (200, 304))

  def listContactsFromUserAddressBooks: ChainBuilder =
    group("listContactsFromUserAddressBooks") {
      foreach("${addressBooksLinks}", "addressBookLink") {
        exec(listContactsFromAddressBook("${addressBookLink}"))
          .pause(1 second)
      }
    }

  def listContactsFromCollectedAddressBook: HttpRequestBuilder =
    listContactsFromAddressBook(s"/addressbooks/$${$UserId}/collected.json")

  def getCollectedAddressBookProperty: HttpRequestBuilder =
    http("Get collected address book property")
      .httpRequest("PROPFIND", s"/dav/api/addressbooks/$${$UserId}/collected.json")
      .check(status is 200)

  def getOneCreatedContactInCollectedAddressBook: HttpRequestBuilder =
    http("get one single created contact in collected address book")
      .get(s"/dav/api/addressbooks/$${$UserId}/collected/$${$ContactUuid}.vcf")
      .check(status in (200, 304))
}