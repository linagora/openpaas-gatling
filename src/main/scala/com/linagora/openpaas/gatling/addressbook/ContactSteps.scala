package com.linagora.openpaas.gatling.addressbook

import com.linagora.openpaas.gatling.Configuration.ContactCount
import com.linagora.openpaas.gatling.addressbook.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.concurrent.duration.DurationInt

object ContactSteps {
  def createContactInDefaultAddressBook(): HttpRequestBuilder = {
    http("createContactsInDefaultAddressBook")
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

  def listContactsFromAddressBook(addressBookLink: String): HttpRequestBuilder =
    http(s"load contacts from address book $addressBookLink")
      .get(s"/dav/api$addressBookLink?limit=20&offset=0&sort=fn")
      .check(status in (200, 304))

  def listContactsFromUserAddressBooks(): ChainBuilder =
    group("listContactsFromUserAddressBooks") {
      foreach(s"$${$AddressBooksLinks}", s"$${$AddressBookLink}") {
        exec(listContactsFromAddressBook(s"$${$AddressBookLink}"))
          .pause(1 second)
      }
    }

  def listContactsFromCollectedAddressBook(): HttpRequestBuilder =
    listContactsFromAddressBook(s"/addressbooks/$${$UserId}/collected.json")

  def getOneCreatedContactInCollectedAddressBook(): HttpRequestBuilder =
    http("getOneCreatedContactInCollectedAddressBook")
      .get(s"/dav/api/addressbooks/$${$UserId}/collected/$${$ContactUuid}.vcf")
      .check(status in (200, 304))

  def provisionContacts(): ChainBuilder = {
    val contactUuidFeeder = Iterator.continually(Map("contactUuid" -> randomUuidString))

    group("provisionContacts") {
      repeat(ContactCount) {
        feed(contactUuidFeeder)
          .exec(createContactInDefaultAddressBook())
          .pause(1 second)
      }
    }
  }
}