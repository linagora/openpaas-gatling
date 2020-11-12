package com.linagora.openpaas.gatling.core.authentication.lemonldap

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import LemonLdapTemplateRequestsList._
import com.linagora.openpaas.gatling.Configuration._

object LemonLdapSteps {
  def loadLoginTemplates: ChainBuilder =
    group("Load LemonLDAP authentication portal") {
      repeat(authLoginPageTemplates.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = authLoginPageTemplates(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"${LemonLDAPPortalUrl}$${resourceURL}")
            .check(status in(200, 304)))
      }
    }

  def getPage: HttpRequestBuilder =
    http("Get LemonLDAP login page")
      .get(LemonLDAPPortalUrl)
      .disableFollowRedirect
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
      ))
      .check(status in (200, 304))
      .check(bodyString.saveAs("BODY"))
      .check(css("input[name='token']", "value").saveAs(LemonLdapFormToken))

  def login: HttpRequestBuilder =
    http("Login through LemonLDAP")
      .post(LemonLDAPPortalUrl)
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1",
        "Content-Type" -> "application/x-www-form-urlencoded"))
      .formParam("url", "")
      .formParam("timezone", "1")
      .formParam("skin", "bootstrap")
      .formParam("user", s"$${$UsernameSessionParam}")
      .formParam("password", s"$${$PasswordSessionParam}")
      .formParam("token", s"$${$LemonLdapFormToken}")
      .check(status is 200)

  def goToOpenPaaSApplication: HttpRequestBuilder =
    http("Go to OpenPaaS application")
      .get("/")
      .check(status in (200, 304))
}
