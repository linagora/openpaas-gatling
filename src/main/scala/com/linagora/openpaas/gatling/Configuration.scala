package com.linagora.openpaas.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

object Configuration {
  val ServerHostName = "127.0.0.1"
  val Port = 8080
  val BaseOpenPaaSUrl = s"http://$ServerHostName:$Port"

  val httpProtocol = http
    .baseUrl(Configuration.BaseOpenPaaSUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")
    .userAgentHeader("Gatling")
    .wsBaseUrl(s"ws://$ServerHostName:$Port/socket.io")

  val JmapHostName = "127.0.0.1"
  val JmapPort = 1080
  val JmapBaseUrl = s"http://$JmapHostName:$JmapPort"

  val ScenarioDuration = 10 second
  val UserCount = 1

  val PlatformAdminLogin = "admin@open-paas.org"
  val PlatformAdminPassword = "secret"

  val DomainName = "gatling-openpaas.org"
  val DomainAdminEmail = s"admin@${DomainName}"
  val DomainAdminPassword = "secret"
}