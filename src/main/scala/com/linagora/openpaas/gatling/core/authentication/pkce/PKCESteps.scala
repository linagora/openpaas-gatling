package com.linagora.openpaas.gatling.core.authentication.pkce

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

object PKCESteps {
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
      .get(LemonLDAPPortalUrl + s"/oauth2/authorize?client_id=${oidcClient}&redirect_uri=${URLEncoder.encode(oidcCallback, Charsets.UTF_8)}&response_type=code&scope=openid%20offline_access%20email%20profile&state=$${oidc_state}&code_challenge=$${pkce_code_challenge}&code_challenge_method=${pkceCodeChallengeMethod}&response_mode=query")
      .disableFollowRedirect
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
      ))
      .check(status in (200, 304))
      .check(css("input[name='token']", "value").saveAs(LemonLdapFormToken))

  def login: HttpRequestBuilder =
    http("Login through LemonLDAP")
      .post(LemonLDAPPortalUrl + s"/oauth2/authorize?client_id=${oidcClient}&redirect_uri=${URLEncoder.encode(oidcCallback, Charsets.UTF_8)}&response_type=code&scope=openid%20offline_access%20email%20profile&state=$${oidc_state}&code_challenge=$${pkce_code_challenge}&code_challenge_method=${pkceCodeChallengeMethod}&response_mode=query#")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1",
        "Content-Type" -> "application/x-www-form-urlencoded"))
      .formParam("url", "aHR0cDovL2wtYXV0aC5vcGVuLXBhYXMub3JnLy9vYXV0aDI=")
      .formParam("timezone", "1")
      .formParam("skin", "bootstrap")
      .formParam("user", s"$${$UsernameSessionParam}")
      .formParam("password", s"$${$PasswordSessionParam}")
      .formParam("token", s"$${$LemonLdapFormToken}")
      .disableFollowRedirect
      .check(status.is(302),
        header("Location")
          .transform(extractAuthorizationCodeFromLocation _)
          .saveAs("authorization_code"))

   def getToken: HttpRequestBuilder =
    http("get token")
      .post(LemonLDAPPortalUrl + "/oauth2/token")
      .formParam("client_id", oidcClient)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("code", "${authorization_code}")
      .formParam("redirect_uri", oidcCallback)
      .formParam("code_verifier", "${pkce_code_verifier}")
      .formParam("grant_type", "authorization_code")
      .check(status.is(200),
        jsonPath("$.access_token").find.saveAs("access_token"),
        jsonPath("$.refresh_token").find.saveAs("refresh_token")
      )

  private def extractAuthorizationCodeFromLocation(locationUrl: String): String = {
    Uri.create(locationUrl.replace("/#/","/"))
      .getEncodedQueryParams.asScala.find(_.getName == "code").get.getValue
  }

  def goToOpenPaaSApplication: HttpRequestBuilder =
    http("Go to OpenPaaS application")
      .get("/")
      .check(status in (200, 304))
}
