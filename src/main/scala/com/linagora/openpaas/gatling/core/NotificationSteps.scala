package com.linagora.openpaas.gatling.core

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object NotificationSteps {
  def getUnreadNotifications =
    http("Get unread notifications")
      .get("/api/user/notifications/unread")
      .check(status in (200, 304))
}
