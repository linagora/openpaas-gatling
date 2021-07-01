package com.linagora.openpaas.gatling.core

import java.security.MessageDigest
import java.util.{Base64, UUID}
import com.google.common.base.Charsets
import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.authentication.{AuthenticationStrategy, PKCEToken}
import com.linagora.openpaas.gatling.core.authentication.AuthenticationStrategy.{PKCE, PKCE_WITH_CAS}
import com.linagora.openpaas.gatling.core.authentication.basiclogin.BasicLoginSteps
import com.linagora.openpaas.gatling.core.authentication.lemonldap.LemonLdapSteps
import com.linagora.openpaas.gatling.core.authentication.oidc.OIDCSteps
import com.linagora.openpaas.gatling.core.authentication.pkce.PKCESteps
import com.linagora.openpaas.gatling.core.authentication.pkceWithCas.PKCEWithCasSteps
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder

object LoginSteps {

  def loadLoginTemplates: ChainBuilder = AuthenticationStrategyToUse match {
    case AuthenticationStrategy.LemonLDAP => LemonLdapSteps.loadLoginTemplates
    case AuthenticationStrategy.Basic => BasicLoginSteps.loadLoginTemplates
    case AuthenticationStrategy.OIDC => OIDCSteps.loadLoginTemplates
    case AuthenticationStrategy.PKCE => PKCESteps.loadLoginTemplates
    case AuthenticationStrategy.PKCE_WITH_CAS => PKCEWithCasSteps.loadLoginTemplates
  }

  def login(): ChainBuilder = AuthenticationStrategyToUse match {
    case AuthenticationStrategy.LemonLDAP  =>
      exec(LemonLdapSteps.getPage)
        .exec(LemonLdapSteps.login)
        .exec(LemonLdapSteps.goToOpenPaaSApplication)
    case AuthenticationStrategy.OIDC =>
      exec(session =>
        session.set("oidc_state", "850fe81c89ae4488beec94b60b5f1660")
        .set("oidc_nonce", "5cce5758b2e946038b59d7f21599db70"))
      .exec(OIDCSteps.getPage)
      .exec(OIDCSteps.login)
      .exec(OIDCSteps.goToOpenPaaSApplication)

    case AuthenticationStrategy.PKCE =>
      exec(session => {
        val codeChallengeVerifier = UUID.randomUUID().toString + UUID.randomUUID().toString + UUID.randomUUID().toString
        val codeChallenge = Base64.getUrlEncoder
          .encodeToString(MessageDigest.getInstance("SHA-256").digest(codeChallengeVerifier.getBytes(Charsets.US_ASCII)))
          .split("=")(0)
          .replace("+", "-")
          .replace("/", "_")

        session.set("oidc_state", UUID.randomUUID().toString + UUID.randomUUID().toString)
          .set("pkce_code_challenge",	codeChallenge)
          .set("pkce_code_verifier",	codeChallengeVerifier)
      })
        .exec(PKCESteps.getPage)
        .exec(PKCESteps.login)
        .exec(PKCEToken.getToken)
        .exec(PKCESteps.goToOpenPaaSApplication)

    case AuthenticationStrategy.PKCE_WITH_CAS =>
      exec(session => {
        val codeChallengeVerifier = UUID.randomUUID().toString + UUID.randomUUID().toString + UUID.randomUUID().toString
        val codeChallenge = Base64.getUrlEncoder
          .encodeToString(MessageDigest.getInstance("SHA-256").digest(codeChallengeVerifier.getBytes(Charsets.US_ASCII)))
          .split("=")(0)
          .replace("+", "-")
          .replace("/", "_")

        session.set("oidc_state", UUID.randomUUID().toString + UUID.randomUUID().toString)
          .set("pkce_code_challenge",	codeChallenge)
          .set("pkce_code_verifier",	codeChallengeVerifier)
      })
        .exec(PKCEWithCasSteps.getKeycloakPage)
        .exec(PKCEWithCasSteps.keycloakSamlLogin)
        .exec(PKCEWithCasSteps.casSSO)
        .exec(PKCEWithCasSteps.casLoginPage)
        .exec(PKCEWithCasSteps.loadLoginCasTemplates)
        .exec(PKCEWithCasSteps.login)
        .exec(PKCEWithCasSteps.casProfile)
        .exec(PKCEWithCasSteps.casBrokerEndpoint)
        .exec(PKCEWithCasSteps.obtainAuthorizationCode).exitHereIfFailed
        .exec(PKCEToken.getToken)
        .exec(PKCEWithCasSteps.goToOpenPaaSApplication)

    case AuthenticationStrategy.Basic => exec(BasicLoginSteps.login)
  }

  def logout: ChainBuilder = {
    AuthenticationStrategyToUse match {
      case PKCE => PKCESteps.logout
      case PKCE_WITH_CAS => PKCEWithCasSteps.logout
      case _ =>
        exec(http("Logout")
          .get("/logout")
          .check(status in(200, 302)))
    }
  }
}
