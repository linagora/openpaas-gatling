package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.authentication.AuthenticationStrategy
import com.linagora.openpaas.gatling.core.authentication.basiclogin.BasicLoginSteps
import com.linagora.openpaas.gatling.core.authentication.lemonldap.LemonLdapSteps
import com.linagora.openpaas.gatling.core.authentication.oidc.OIDCSteps
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.structure.ChainBuilder

object LoginSteps {

  def loadLoginTemplates: ChainBuilder = authenticationStrategy match {
    case AuthenticationStrategy.LemonLDAP => LemonLdapSteps.loadLoginTemplates
    case AuthenticationStrategy.Basic => BasicLoginSteps.loadLoginTemplates
    case AuthenticationStrategy.OIDC => BasicLoginSteps.loadLoginTemplates
  }

  def login(): ChainBuilder = authenticationStrategy match {
    case AuthenticationStrategy.LemonLDAP  =>
      exec(LemonLdapSteps.getPage)
        .exec(LemonLdapSteps.login)
        .exec(LemonLdapSteps.goToOpenPaaSApplication)
    case AuthenticationStrategy.OIDC =>
      exec(session =>
        //TODO use random values
        session.set("oidc_state", "850fe81c89ae4488beec94b60b5f1660")
        .set("oidc_nonce", "5cce5758b2e946038b59d7f21599db70"))
      .exec(OIDCSteps.getPage)
      .exec(OIDCSteps.login)
      .exec(OIDCSteps.goToOpenPaaSApplication)
    case AuthenticationStrategy.Basic => exec(BasicLoginSteps.login)
  }

  def logout: HttpRequestBuilder =
    http("Logout")
      .get("/logout")
      .check(status in(200, 302))
}
