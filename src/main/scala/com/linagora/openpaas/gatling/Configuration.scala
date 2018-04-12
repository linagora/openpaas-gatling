package com.linagora.openpaas.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

object Configuration {
  val ServerHostName = "127.0.0.1"
  val Port = 8080
  val BaseOpenPaaSUrl = s"http://$ServerHostName:$Port"

  val httpProtocol = http
    .baseURL(Configuration.BaseOpenPaaSUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")
      .wsBaseURL(s"ws://$ServerHostName:$Port/socket.io")

  val ScenarioDuration = 10 second
  val UserCount = 1

  val PlatformAdminLogin = "admin@open-paas.org"
  val PlatformAdminPassword = "secret"
  val DomainId = "5acc3648aa0fd100429ec2a3"
  val DomainName = "open-paas.org"
}