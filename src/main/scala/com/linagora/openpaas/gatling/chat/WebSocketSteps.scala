package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.provisionning.SessionKeys.{token, userId}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object WebSocketSteps {
  def openChatConnection =
    ws("WsOpen")
      .open("/chat")
      .queryParam("token", s"$${$token}")
      .queryParam("EIO", "3")
      .queryParam("transport", "websocket")
      .queryParam("user", s"$${$userId}")
}
