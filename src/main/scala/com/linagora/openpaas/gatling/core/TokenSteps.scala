package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object TokenSteps {

  def retrieveAuthenticationToken =
    http("Get Authentication Token")
      .get("/api/authenticationtoken")
      .check(status in (200, 304))
      .check(jsonPath("$.token").saveAs(Token))

  def generateJwtToken: ChainBuilder =
    exec(http("Generate jwt token")
      .post("/api/jwt/generate")
      .check(status is 200)
      .check(bodyString.saveAs(JwtToken))
    )
    .exec(session => {
      val denormalizedJwtToken = session(JwtToken).as[String].replaceAll("\"", "")
      session.set(JwtToken, denormalizedJwtToken)
    })
}
