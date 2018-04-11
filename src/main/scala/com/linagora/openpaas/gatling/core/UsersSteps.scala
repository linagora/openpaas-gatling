package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.response.Response

object UsersSteps {
  val statusCode = "statusCode"

  def checkIfOk(check: HttpCheck): HttpCheck =
    checkIf((r: Response, s: Session) => r.statusCode.contains(200)){check}

  def findUserIdByUsername =
    withAuth(
      http("findUserIdByUsername")
        .get(s"/api/users"))
        .queryParam("email", s"$${$otherUsername}")
        .check(status.in(200, 304).saveAs(statusCode))
        .check(jsonPath("$[0]._id").saveAs(otherUserId))

  def getOtherUserProfile =
    withAuth(
      http("getUser")
          .get(s"/api/users/$${$otherUserId}"))
        .check(status in (200, 304))
}

