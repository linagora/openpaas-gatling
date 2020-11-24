package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.linagora.openpaas.gatling.provisionning.Authentication._

object TokenSteps {

  def retrieveAuthenticationToken =
    exec(withAuth(http("Get Authentication Token")
      .get("/api/authenticationtoken")
      .check(status in (200, 304))
      .check(jsonPath("$.token").saveAs(Token)))
    )

  def generateJwtTokenWithAuth =
    exec(withAuth(http("Generate jwt token")
      .post("/api/jwt/generate")
      .check(status is 200)
      .check(bodyString.saveAs(JwtToken))
    ))
    .exec(session => {
      val denormalizedJwtToken = session(JwtToken).as[String].replaceAll("\"", "")
      session.set(JwtToken, denormalizedJwtToken)
    })
}
