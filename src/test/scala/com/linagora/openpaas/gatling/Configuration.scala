package com.linagora.openpaas.gatling

import scala.concurrent.duration._

object Configuration {
  val ServerHostName = "127.0.0.1"
  val BaseOpenPaaSUrl = s"http://$ServerHostName:80"

  val ScenarioDuration = 1 minutes
  val UserCount = 100

  val PlatformAdminLogin = "admin@test.openpaas.com"
  val PlatformAdminPassword = "0123456789"

}