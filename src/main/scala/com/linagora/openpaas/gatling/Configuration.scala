package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.core.authentication.AuthenticationStrategy
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Properties

object Configuration {
  val OpenPaaSHostName = Properties.envOrElse("OPENPAAS_HOSTNAME", "localhost")
  val OpenPaaSPort = Properties.envOrElse("OPENPAAS_PORT", "8080").toInt
  val OpenPaaSProtocol = Properties.envOrElse("OPENPAAS_PROTOCOL", "http")
  val OpenPaaSBaseUrl = s"$OpenPaaSProtocol://$OpenPaaSHostName:$OpenPaaSPort"

  val SabreHostName = Properties.envOrElse("SABRE_HOSTNAME", "localhost")
  val SabrePort = Properties.envOrElse("SABRE_PORT", "8001").toInt
  val SabreProtocol = Properties.envOrElse("SABRE_PROTOCOL", "http")
  val SabreBaseUrl = s"$SabreProtocol://$SabreHostName:$SabrePort"

  val WebSocketHostName = Properties.envOrElse("WEBSOCKET_HOSTNAME", OpenPaaSHostName)
  val WebSocketPort = Properties.envOrElse("WEBSOCKET_PORT", s"${OpenPaaSPort}").toInt
  val WebSocketProtocol = Properties.envOrElse("WEBSOCKET_PROTOCOL", "ws")
  val WebSocketBaseUrl = s"$WebSocketProtocol://$WebSocketHostName:$WebSocketPort/socket.io"

  val JmapHostName = Properties.envOrElse("JMAP_HOSTNAME", OpenPaaSHostName)
  val JmapPort = Properties.envOrElse("JMAP_PORT", "1080").toInt
  val JmapProtocol = Properties.envOrElse("JMAP_PROTOCOL", OpenPaaSProtocol)
  val JmapBaseUrl = s"$JmapProtocol://$JmapHostName:$JmapPort"

  val authenticationStrategy = AuthenticationStrategy.fromConfiguration(Properties.envOrElse("AUTHENTICATION_STRATEGY", "basic")).get
  val lemonLDAPPortalProtocol = Properties.envOrElse("LEMONLDAP_PORTAL_PROTOCOL", OpenPaaSProtocol)
  val lemonLDAPPortalHostName = Properties.envOrElse("LEMONLDAP_PORTAL_HOSTNAME", "auth.latest.integration-open-paas.org")
  val LemonLDAPPortalUrl = s"$lemonLDAPPortalProtocol://$lemonLDAPPortalHostName"

  val oidcClient = Properties.envOrElse("OIDC_CLIENT", "openpaas")
  val oidcCallback = OpenPaaSBaseUrl + Properties.envOrElse("OIDC_CALLBACK", "/inbox/#/auth/oidc/callback")

  val pkceCodeChallengeMethod = "S256"

  val httpProtocol = http
    .baseUrl(OpenPaaSBaseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json; charset=UTF-8")
    .userAgentHeader("Gatling")
    .wsBaseUrl(WebSocketBaseUrl)

  val InjectDuration = Properties.envOrElse("INJECT_DURATION", "10").toInt seconds
  val ScenarioDuration = Properties.envOrElse("SCENARIO_DURATION", "10").toInt seconds
  val UserCount = Properties.envOrElse("USER_COUNT", "1").toInt
  val ContactCount = 20
  val CalendarCount = 2
  val EventCount = 20
  val EmailCount = 20
  val EventAttendeeCount = 100
  
  val humanActionMinDelay = Properties.envOrElse("HUMAN_ACTION_MIN_DELAY", "7").toInt
  val humanActionMaxDelay = Properties.envOrElse("HUMAN_ACTION_MAX_DELAY", "15").toInt

  val PlatformAdminLogin = Properties.envOrElse("PLATFORM_ADMIN_USER", "admin@open-paas.org")
  val PlatformAdminPassword = Properties.envOrElse("PLATFORM_ADMIN_PWD", "ah! ah!")

  val DomainName = "sandbox.integration-open-paas.org"
  val DomainAdminEmail = s"admin@${DomainName}"
  val DomainAdminPassword = "secret"
}