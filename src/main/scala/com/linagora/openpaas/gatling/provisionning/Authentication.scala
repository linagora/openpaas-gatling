package com.linagora.openpaas.gatling.provisionning

import io.gatling.core.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder


object Authentication {
  val UsernameSessionParam = "username"
  val PasswordSessionParam = "password"

  def withAuth(request: HttpRequestBuilder): HttpRequestBuilder =
    request.basicAuth(s"$${$UsernameSessionParam}", s"$${$PasswordSessionParam}")
}
