package com.linagora.openpaas.gatling.provisionning

import com.linagora.openpaas.gatling.Configuration
import com.linagora.openpaas.gatling.core.authentication.AuthenticationStrategy
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object Authentication {

  def withAuth(request: HttpRequestBuilder): HttpRequestBuilder = {
    Configuration.authenticationStrategy match {
      case AuthenticationStrategy.Basic =>
        request.basicAuth(s"$${$UsernameSessionParam}", s"$${$PasswordSessionParam}")
      case AuthenticationStrategy.OIDC => request
        .header("Origin", Configuration.OpenPaaSBaseUrl)
        .header("Authorization", "Bearer ${access_token}")
      case _ => request
    }
  }

}
