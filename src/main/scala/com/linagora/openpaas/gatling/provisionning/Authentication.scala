package com.linagora.openpaas.gatling.provisionning

import com.linagora.openpaas.gatling.Configuration
import com.linagora.openpaas.gatling.Configuration.AuthenticationStrategyToUse
import com.linagora.openpaas.gatling.core.LoginSteps
import com.linagora.openpaas.gatling.core.authentication.{AuthenticationStrategy, PKCEToken}
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.HttpRequestBuilder

object Authentication {

  def withAuth(request: HttpRequestBuilder): ChainBuilder = {
    Configuration.AuthenticationStrategyToUse match {
      case AuthenticationStrategy.Basic =>
        exec(request.basicAuth(s"$${$UsernameSessionParam}", s"$${$PasswordSessionParam}"))
      case AuthenticationStrategy.OIDC => exec(withBearer(request))
      case AuthenticationStrategy.PKCE => exec(withBearer(request))
      case AuthenticationStrategy.PKCE_WITH_CAS => exec(withBearer(request))
      case _ => exec(request)
    }
  }

  private def withBearer(request: HttpRequestBuilder): ChainBuilder = {
    exec(renewTokenIfNeeded())
      .exec(request
          .header("Origin", Configuration.OpenPaaSBaseUrl)
          .header("Authorization", "Bearer ${access_token}"))
  }

  private def renewTokenIfNeeded(): ChainBuilder = AuthenticationStrategyToUse match {
    case AuthenticationStrategy.PKCE => exec(PKCEToken.renewTokenIfNeeded)
    case AuthenticationStrategy.PKCE_WITH_CAS => exec(PKCEToken.renewTokenIfNeeded)
    case _ => exec()
  }
}
