package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object TokenSteps {
  def retrieveToken =
    withAuth(http("getToken")
      .get("/api/authenticationtoken"))
      .check(status in (200, 304))
      .check(jsonPath("$.token").saveAs(token))
}
