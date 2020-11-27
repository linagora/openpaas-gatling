package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.http.request.builder.HttpRequestBuilder
import scala.concurrent.duration.DurationInt

object DomainSteps {
  def listDomains: HttpRequestBuilder =
    http("list ESN domains")
      .get("/api/domains")
      .basicAuth(PlatformAdminLogin, PlatformAdminPassword)
      .check(status in(200, 304))
      .check(jsonPath("$[*].id").findAll.saveAs("domainIds"))
      .check(jsonPath("$[*].name").findAll.saveAs("domainNames"))

  def createGatlingTestDomain: HttpRequestBuilder =
    http("create domain")
      .post("/api/domains")
      .basicAuth(PlatformAdminLogin, PlatformAdminPassword)
      .body(StringBody(s"""{"name": "${DomainName}", "company_name": "Linagora", "administrator": {"email": "${DomainAdminEmail}", "password": "${DomainAdminPassword}" }}"""))
      .check(status is 201)
      .check(jsonPath("$.id").saveAs(DomainId))

  def getDomain: HttpRequestBuilder =
    http("get domain by ID")
      .get(s"/api/domains/$${$DomainId}")
      .check(status in (200, 304))

  def getThemeForDomain(): HttpRequestBuilder =
    http("getThemeForDomain")
      .get(s"/api/themes/$${$DomainId}")
      .check(status in (200, 304))

  def getLogoForDomain: HttpRequestBuilder =
    http("getLogoForDomain")
      .get(s"/api/themes/$${$DomainId}/logo")
      .check(status in (200, 304))

  def createGatlingTestDomainIfNotExist: ChainBuilder =
    exec(listDomains)
      .pause(1 second)
      .doIfOrElse(session => session("domainNames").as[Vector[String]].contains(DomainName)) {
        exec(session => {
          val index = session("domainNames").as[Vector[String]].indexOf(DomainName)
          val domainIds = session(DomainIds).as[Vector[String]]
          session.set(DomainId, domainIds(index))
        })
      } {
        exec(createGatlingTestDomain)
      }
}
