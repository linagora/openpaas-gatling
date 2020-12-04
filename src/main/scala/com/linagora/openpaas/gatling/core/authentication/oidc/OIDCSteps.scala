package com.linagora.openpaas.gatling.core.authentication.oidc

import java.net.URLEncoder

import com.google.common.base.Charsets
import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.core.authentication.lemonldap.LemonLdapTemplateRequestsList._
import io.gatling.http.client.ahc.uri.Uri

import scala.collection.JavaConverters._

object OIDCSteps {
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

  def login: HttpRequestBuilder =
    http("Login through LemonLDAP")
      .post(LemonLDAPPortalUrl  + s"/oauth2/authorize?client_id=${OidcClient}&redirect_uri=${URLEncoder.encode(OidcCallback, Charsets.UTF_8)}&response_type=id_token%20token&scope=openid%20email%20profile&state=$${oidc_state}&nonce=$${oidc_nonce}")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1",
        "Content-Type" -> "application/x-www-form-urlencoded"))
      .formParam("url", "")
      .formParam("timezone", "1")
      .formParam("skin", "bootstrap")
      .formParam("user", s"$${$UsernameSessionParam}")
      .formParam("password", s"$${$PasswordSessionParam}")
      .check(status.is(302),
        header("Location")
          .transform(extractAccessTokenFromLocation _)
          .saveAs("access_token"))

  private def extractAccessTokenFromLocation(locationUrl: String): String = {
    Uri.create(locationUrl.replace("/#/","/").replace("callback#","callback?"))
      .getEncodedQueryParams.asScala.find(_.getName == "access_token").get.getValue
  }

  def goToOpenPaaSApplication: HttpRequestBuilder =
    http("Go to OpenPaaS application")
      .get("/")
      //.disableFollowRedirect
      .check(status in (200, 304))
}
