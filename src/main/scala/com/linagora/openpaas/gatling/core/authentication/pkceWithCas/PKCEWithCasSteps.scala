package com.linagora.openpaas.gatling.core.authentication.pkceWithCas

import com.google.common.base.Charsets
import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.authentication.keycloak.KeycloakTemplateRequestList._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef.{css, _}
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.client.ahc.uri.Uri
import io.gatling.http.request.builder.HttpRequestBuilder

import java.net.URLEncoder
import java.util.UUID
import scala.collection.JavaConverters._

object PKCEWithCasSteps {

  private val logoutCasAssets = List(
    "/webjars/jquery/3.3.1-1/jquery.min.js",
    "/webjars/zxcvbn/4.3.0/zxcvbn.js",
    "/webjars/jquery-ui/1.12.1/jquery-ui.min.js",
    "/webjars/jquery-cookie/1.4.1-1/jquery.cookie.js",
    "/webjars/bootstrap/4.1.3/js/bootstrap.bundle.min.js",
    "/webjars/headjs/1.0.3/head.min.js",
    "/js/cas.js",
    "/images/cas-logo.png",
    "/webjars/font-awesome/5.6.1/css/all.min.css",
    "/css/cas.css",
    "/favicon.ico"
  )

  private val loginCasPreprodAssets = List(
    "/webjars/font-awesome/5.6.1/css/all.min.css",
    "/css/cas.css",
    "/webjars/jquery/3.3.1-1/jquery.min.js",
    "/webjars/zxcvbn/4.3.0/zxcvbn.js",
    "/webjars/jquery-ui/1.12.1/jquery-ui.min.js",
    "/webjars/jquery-cookie/1.4.1-1/jquery.cookie.js",
    "/webjars/bootstrap/4.1.3/js/bootstrap.bundle.min.js",
    "/webjars/headjs/1.0.3/head.min.js",
    "/js/cas.js",
    "/images/webapp.png",
    "/webjars/font-awesome/5.6.1/webfonts/fa-solid-900.woff2",
    "/webjars/font-awesome/5.6.1/webfonts/fa-regular-400.woff2",
    "/favicon.ico"
  )

  def loadLoginTemplates: ChainBuilder =
    loadAssets("Load Keycloak authentication portal", authLoginPageTemplates.toList, KeycloakPortalUrl)

  def loadLoginCasTemplates: ChainBuilder =
    loadAssets("Load CAS authentication portal", loginCasPreprodAssets, CasBaseUrl)

  def loadLogoutCasTemplates: ChainBuilder =
    loadAssets("Load CAS disconnected portal", logoutCasAssets, CasBaseUrl)

  private def loadAssets(groupName: String, assets: List[String], baseUrl: String): ChainBuilder = {
    val counterName = "counter_"  + UUID.randomUUID().toString
    group(groupName) {
      repeat(assets.length, counterName) {
        exec(session => {
          val index = session(counterName).as[Int]
          val resourceURL = assets(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"${baseUrl}$${resourceURL}")
            .check(status in(200, 304)))
      }.exec(session => session.remove(counterName))
    }
  }

  def getLemonPage: HttpRequestBuilder =
    http("Get LemonLDAP login page")
      .get(LemonLDAPPortalUrl + s"/oauth2/authorize?client_id=${OidcClient}&redirect_uri=${URLEncoder.encode(OidcCallback, Charsets.UTF_8)}&response_type=code&scope=openid%20email%20profile&state=$${oidc_state}&code_challenge=$${pkce_code_challenge}&code_challenge_method=${PkceCodeChallengeMethod}&response_mode=query")
      .disableFollowRedirect
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
      ))
      .check(status in (200),
        css("input[name='RelayState']", "value").saveAs("cas_relay_state"),
        css("input[name='SAMLRequest']", "value").saveAs("cas_saml_request")
      )

  def getKeycloakPage: HttpRequestBuilder =
    http("Get Keycloak login page")
      .get(KeycloakPortalUrl + s"/auth/realms/${KeycloakRealm}/protocol/openid-connect/auth")
      .queryParam("client_id", s"${OidcClient}")
      .queryParam("redirect_uri", s"${URLEncoder.encode(OidcCallback, Charsets.UTF_8)}")
      .queryParam("response_type", "code")
      .queryParam("scope", "openid email profile")
      .queryParam("state", s"$${oidc_state}")
      .queryParam("code_challenge", s"$${pkce_code_challenge}")
      .queryParam("code_challenge_method", s"${PkceCodeChallengeMethod}")
      .queryParam("response_mode", "query")
      .disableFollowRedirect
      .check(status.is(303))
      .check(header("Location").saveAs("keycloak_saml_login"))

  def keycloakSamlLogin: HttpRequestBuilder =
    http("Get Keycloak SAML login page")
      .get("${keycloak_saml_login}")
      .check(status.is(200),
          css("input[name='RelayState']", "value").saveAs("cas_relay_state"),
          css("input[name='SAMLRequest']", "value").saveAs("cas_saml_request")
      )

  def casSSO: HttpRequestBuilder =
    http("get CAS SSO")
      .post(CasBaseUrl + "/idp/profile/SAML2/POST/SSO")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1",
        "Content-Type" -> "application/x-www-form-urlencoded"
      ))
      .formParam("RelayState", "${cas_relay_state}")
      .formParam("SAMLRequest", "${cas_saml_request}")
      .disableFollowRedirect
      .check(status.is(302), header("Location")
        .saveAs("cas_login_page"))


  def casLoginPage: HttpRequestBuilder =  http("get CAS Login Page")
    .get("${cas_login_page}")
    .check(status.is(200), css("input[name='execution']", "value").saveAs("cas_execution"))


  def login: HttpRequestBuilder =
    http("Login through CAS")
      .post("${cas_login_page}")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1",
        "Content-Type" -> "application/x-www-form-urlencoded"))
      .formParam("execution", "${cas_execution}")
      .formParam("geolocation", "")
      .formParam("_eventId", "submit")
      .formParam("username", s"$${$UsernameForLoginSessionParam}")
      .formParam("password", s"$${$PasswordSessionParam}")
      .disableFollowRedirect
      .check(status.is(302),
        header("Location").saveAs("cas_profile"))

  def casProfile: HttpRequestBuilder =
    http("CAS profile")
      .get("${cas_profile}")
      .disableFollowRedirect
      .check(status.is(200),
        css("input[name='RelayState']", "value").saveAs("cas_relay_state_response"),
        css("input[name='SAMLResponse']", "value").saveAs("cas_saml_response"))

  def casBrokerEndpoint: HttpRequestBuilder =
    http("CAS broker endpoint on post")
      .post(KeycloakPortalUrl + s"/auth/realms/${KeycloakRealm}/broker/saml/endpoint")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1",
        "Content-Type" -> "application/x-www-form-urlencoded"))
      .formParam("RelayState", "${cas_relay_state_response}")
      .formParam("SAMLResponse", "${cas_saml_response}")
      .disableFollowRedirect
      .check(status.is(302),
        header("Location").saveAs("obtain_code_next_step"))

  def obtainAuthorizationCode: ChainBuilder =
    doIfOrElse(session => session("obtain_code_next_step").as[String].contains("first-broker-login")) {
      exec(firstBrokerLogin)
        .exec(afterFirstBrokerLogin)
    } {
      exec { session =>
        session.set("authorization_code", extractAuthorizationCodeFromLocation(session("obtain_code_next_step").as[String]))
      }
    }

  def firstBrokerLogin: HttpRequestBuilder =
    http("CAS first broker login")
      .get("${obtain_code_next_step}")
      .disableFollowRedirect
      .check(status.is(302),
        header("Location").saveAs("after_first_broker_login"))

  def afterFirstBrokerLogin: HttpRequestBuilder =
    http("CAS get auth code")
      .get("${after_first_broker_login}")
      .disableFollowRedirect
      .check(status.is(302),
        header("Location")
          .transform(extractAuthorizationCodeFromLocation _)
          .saveAs("authorization_code"))

  private def extractAuthorizationCodeFromLocation(locationUrl: String): String = {
    Uri.create(locationUrl.replace("/#/","/"))
      .getEncodedQueryParams.asScala.find(_.getName == "code").get.getValue
  }

  def goToOpenPaaSApplication: HttpRequestBuilder =
    http("Go to OpenPaaS application")
      .get("/inbox")
      .check(status is 200)

  def logout: ChainBuilder = {
    exec(logoutGoToConfirmationPage)
      .exec(logoutCasSLO)
      .exec(logoutCasLandingPage).doIf(session => session("logout_status").value.asOption[Int].contains(500))(exec(flushCookieJar))
      .exec(loadLogoutCasTemplates)
  }

  private def logoutCasLandingPage = {
    http("logout CAS landing page")
      .get(CasBaseUrl + "/logout")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1"
      ))
      .check(status.saveAs("logout_status"))
  }

  private def logoutCasSLO = {
    http("logout CAS SLO")
      .post(CasBaseUrl + "/idp/profile/SAML2/POST/SLO")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1",
        "Content-Type" -> "application/x-www-form-urlencoded"
      ))
      .formParam("SAMLRequest", "${cas_logout_saml_request}")
      .formParam("RelayState", "${cas_logout_relay_state}")
      .disableFollowRedirect
      .check(status.is(302), header("Location")
        .saveAs("cas_login_page"))
  }

  private def logoutGoToConfirmationPage = {
    http("Logout go to confirmation page")
      .get(KeycloakPortalUrl + s"/auth/realms/${KeycloakRealm}/protocol/openid-connect/logout")
      .headers(Map(
        "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "upgrade-insecure-requests" -> "1"))
      .queryParam("id_token_hint", "${id_token}")
      .queryParam("post_logout_redirect_uri", s"${OpenPaaSBaseUrl}/${InboxSpaPath}")
      .disableFollowRedirect
      .check(status is 200, //css("input[name='confirm']", "value").saveAs("logout_confirm"))
        css("input[name='RelayState']", "value").saveAs("cas_logout_relay_state"),
        css("input[name='SAMLRequest']", "value").saveAs("cas_logout_saml_request"))
  }
}
