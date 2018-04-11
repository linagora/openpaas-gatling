package com.linagora.openpaas.gatling.provisionning

import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object Authentication {

  def withAuth(request: HttpRequestBuilder): HttpRequestBuilder =
    request.basicAuth(s"$${$UsernameSessionParam}", s"$${$PasswordSessionParam}")
}
