package com.linagora.openpaas.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Properties

object Configuration {
  val OpenPaaSHostName = Properties.envOrElse("OPENPAAS_HOSTNAME", "localhost")
  val OpenPaaSPort = Properties.envOrElse("OPENPAAS_PORT", "8080").toInt
  val OpenPaaSProtocol = Properties.envOrElse("OPENPAAS_PROTOCOL", "http")
  val OpenPaaSBaseUrl = s"$OpenPaaSProtocol://$OpenPaaSHostName:$OpenPaaSPort"

  val WebSocketHostName = Properties.envOrElse("WEBSOCKET_HOSTNAME", OpenPaaSHostName)
  val WebSocketPort = Properties.envOrElse("WEBSOCKET_PORT", s"${OpenPaaSPort}").toInt
  val WebSocketProtocol = Properties.envOrElse("WEBSOCKET_PROTOCOL", "ws")
  val WebSocketBaseUrl = s"$WebSocketProtocol://$WebSocketHostName:$WebSocketPort/socket.io"

  val JmapHostName = Properties.envOrElse("JMAP_HOSTNAME", OpenPaaSHostName)
  val JmapPort = Properties.envOrElse("JMAP_PORT", "1080").toInt
  val JmapProtocol = Properties.envOrElse("JMAP_PROTOCOL", OpenPaaSProtocol)
  val JmapBaseUrl = s"$JmapProtocol://$JmapHostName:$JmapPort"

  val httpProtocol = http
    .baseUrl(OpenPaaSBaseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")
    .userAgentHeader("Gatling")
    .wsBaseUrl(WebSocketBaseUrl)

  val ScenarioDuration = 10 second
  val UserCount = 1
  val ContactCount = 20
  val EventCount = 20
  val EmailCount = 20

  val PlatformAdminLogin = "admin@open-paas.org"
  val PlatformAdminPassword = "secret"

  val DomainName = "gatling-openpaas.org"
  val DomainAdminEmail = s"admin@${DomainName}"
  val DomainAdminPassword = "secret"
}