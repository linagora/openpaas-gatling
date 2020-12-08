package com.linagora.openpaas.gatling.core.authentication.pkce

import java.net.URLEncoder
import java.util.Base64
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
      .get(LemonLDAPPortalUrl + s"/oauth2/authorize?client_id=${OidcClient}&redirect_uri=${URLEncoder.encode(OidcCallback, Charsets.UTF_8)}&response_type=code&scope=openid%20email%20profile&state=$${oidc_state}&code_challenge=$${pkce_code_challenge}&code_challenge_method=${PkceCodeChallengeMethod}&response_mode=query")
      .disableFollowRedirect
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
      ))
      .check(status in (200, 304))

  def login: HttpRequestBuilder =
    http("Login through LemonLDAP")
      .post(LemonLDAPPortalUrl + s"/oauth2/authorize?client_id=${OidcClient}&redirect_uri=${URLEncoder.encode(OidcCallback, Charsets.UTF_8)}&response_type=code&scope=openid%20email%20profile&state=$${oidc_state}&code_challenge=$${pkce_code_challenge}&code_challenge_method=${PkceCodeChallengeMethod}&response_mode=query#")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1",
        "Content-Type" -> "application/x-www-form-urlencoded"))
      .formParam("url", Base64.getEncoder.encodeToString((LemonLDAPPortalUrl + "//oauth2").getBytes(Charsets.UTF_8)))
      .formParam("timezone", "1")
      .formParam("skin", "bootstrap")
      .formParam("user", s"$${$UsernameSessionParam}")
      .formParam("password", s"$${$PasswordSessionParam}")
      .disableFollowRedirect
      .check(status.is(302),
        header("Location")
          .transform(extractAuthorizationCodeFromLocation _)
          .saveAs("authorization_code"))

  private def extractAuthorizationCodeFromLocation(locationUrl: String): String = {
    Uri.create(locationUrl.replace("/#/","/"))
      .getEncodedQueryParams.asScala.find(_.getName == "code").get.getValue
  }

  private def logoutGoToConfirmationPage = {
    http("Logout go to confirmation page")
      .get(LemonLDAPPortalUrl + "/oauth2/logout")
      .queryParam("id_token_hint", "${id_token}")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1"))
      .queryParam("post_logout_redirect_uri", s"${OpenPaaSBaseUrl}/${InboxSpaPath}")
      .check(status is 200, css("input[name='confirm']", "value").saveAs("logout_confirm"))
  }

  private def logoutConfirm = {
    exec(http("Logout")
      .get(LemonLDAPPortalUrl+"/oauth2/logout")
      .queryParam("id_token_hint", "${id_token}")
      .queryParam("confirm", "${logout_confirm}")
      .queryParam("post_logout_redirect_uri", s"${OpenPaaSBaseUrl}/${InboxSpaPath}")
      .check(status in(200, 302)))
  }

  def logout: ChainBuilder = {
    exec(logoutGoToConfirmationPage)
      .exec(logoutConfirm)
  }

  def goToOpenPaaSApplication: HttpRequestBuilder =
    http("Go to OpenPaaS application")
      .get("/")
      .check(status in (200, 304))
}
