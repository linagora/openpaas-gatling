package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.core.LoginTemplateRequestsList._
import io.gatling.core.structure.ChainBuilder

object LoginSteps {

  def loadLoginTemplates: ChainBuilder =
    group("Load login templates") {
      repeat(loadTemplate.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = loadTemplate(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in(200, 304)))
      }
    }

  def login(): HttpRequestBuilder =
    http("Login")
    .post("/api/login")
    .body(StringBody(s"""{"username":"$${$UsernameSessionParam}","password":"$${$PasswordSessionParam}","rememberme":false,"recaptcha":{"needed":false,"data":null}}"""))
      .check(status.is(200))
      .check(jsonPath("$._id").saveAs(UserId))

  def logout: HttpRequestBuilder =
    http("Logout")
      .get("/logout")
      .check(status in(200, 302))
}
