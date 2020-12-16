package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.linagora.openpaas.gatling.provisionning.Authentication._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.HttpRequestBuilder

object TokenSteps {


  def withTokenAuth(request: HttpRequestBuilder, tokenName: String = "ESNToken"): ChainBuilder = {
    exec(renewTokenIfNeeded)
      .exec(request.header(tokenName, s"$${$Token}"))
  }

  private def renewTokenIfNeeded: ChainBuilder =
    doIf(session => {
      val expiresInInSeconds: Int = session("esn_token_ttl").validate[Int].toOption.getOrElse(60)
      val tokenAcquisitionTime = session("esn_token_acquisition_time").validate[Long].toOption
      tokenAcquisitionTime match {
        case None => true //token never acquired
        case Some(last) if timeInMillis() >= (last + expiresInInSeconds * 1000) => true
        case Some(_) => false
      }
    })(exec(retrieveAuthenticationToken))


  private def retrieveAuthenticationToken: ChainBuilder =
    exec(withAuth(http("Get Authentication Token")
      .get("/api/authenticationtoken")
      .check(status in(200, 304))
      .check(
        jsonPath("$.token").saveAs(Token),
        jsonPath("$.ttl").saveAs("esn_token_ttl"))))
      .exec(session => session.set("esn_token_acquisition_time", timeInMillis().toString))


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

  private def timeInMillis(): Long = System.nanoTime() / 1000000
}
