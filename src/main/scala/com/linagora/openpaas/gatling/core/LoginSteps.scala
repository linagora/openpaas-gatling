package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.core.authentication.basiclogin.BasicLoginSteps
import com.linagora.openpaas.gatling.core.authentication.lemonldap.LemonLdapSteps
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.structure.ChainBuilder

object LoginSteps {

  def loadLoginTemplates: ChainBuilder =
    if (authenticationStrategy.equals("lemonldap"))
      LemonLdapSteps.loadLoginTemplates
    else
      BasicLoginSteps.loadLoginTemplates

  def login(): ChainBuilder=
    if (authenticationStrategy.equals("lemonldap"))
      exec(LemonLdapSteps.login)
      .exec(LemonLdapSteps.goToOpenPaaSApplication)
    else
      exec(BasicLoginSteps.login)

  def logout: HttpRequestBuilder =
    http("Logout")
      .get("/logout")
      .check(status in(200, 302))
}
