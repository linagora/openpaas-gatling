package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.SessionKeys.{Token, UserId}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object WebSocketSteps {
  def openConnection =
    ws("WsOpen")
      .connect("/")
      .queryParam("token", s"$${$Token}")
      .queryParam("EIO", "3")
      .queryParam("transport", "websocket")
      .queryParam("user", s"$${$UserId}")
}
