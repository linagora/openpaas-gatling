package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.authentication.AuthenticationStrategy
import com.linagora.openpaas.gatling.core.authentication.basiclogin.BasicLoginSteps
import com.linagora.openpaas.gatling.core.authentication.lemonldap.LemonLdapSteps
import com.linagora.openpaas.gatling.core.authentication.oidc.OIDCSteps
import com.linagora.openpaas.gatling.core.authentication.pkce.PKCESteps
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.structure.ChainBuilder

object LoginSteps {

  def loadLoginTemplates: ChainBuilder = authenticationStrategy match {
    case AuthenticationStrategy.LemonLDAP => LemonLdapSteps.loadLoginTemplates
    case AuthenticationStrategy.Basic => BasicLoginSteps.loadLoginTemplates
    case AuthenticationStrategy.OIDC => OIDCSteps.loadLoginTemplates
    case AuthenticationStrategy.PKCE => PKCESteps.loadLoginTemplates
  }

  def login(): ChainBuilder = authenticationStrategy match {
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
      exec(session =>
        session.set("oidc_state", "bb64de2918074088b15ea6d5785d7181")
          .set("pkce_code_challenge",	"GO6h862pJDkgMx2DGQuoNHNNm6nlgaa17rUGDPXm7W4")
          .set("pkce_code_verifier",	"d8770d095e454d10bafa36447f17ac143580aa7ad13a4536904333bd0b71bc7d598de2cec8d14b20b81fcaccb9874fe5"))
        .exec(PKCESteps.getPage)
        .exec(PKCESteps.login)
        .exec(PKCESteps.getToken)
        .exec(PKCESteps.goToOpenPaaSApplication)

    case AuthenticationStrategy.Basic => exec(BasicLoginSteps.login)
  }

  def logout: HttpRequestBuilder =
    http("Logout")
      .get("/logout")
      .check(status in(200, 302))
}
