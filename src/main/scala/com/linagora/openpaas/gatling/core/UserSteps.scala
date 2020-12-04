package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.http.Predef._
import io.gatling.core.Predef._

object UserSteps {
  def getProfile() =
    withAuth(
      http("Get profile")
        .get("/api/user"))
      .check(status.in(200, 304))
      .check(jsonPath("$._id").exists)
      .check(jsonPath("$.domains[0].domain_id").exists)
      .check(jsonPath("$._id").saveAs(UserId))
      .check(jsonPath("$.domains[0].domain_id").saveAs(DomainId))
}
