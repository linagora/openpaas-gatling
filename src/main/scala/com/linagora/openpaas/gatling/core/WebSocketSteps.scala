package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.SessionKeys.{Token, UserId}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.ws.{WsTextFrameCheck}

import scala.concurrent.duration.DurationInt

object WebSocketSteps {
  def openConnection(url: String = "/", transport: String = "websocket") =
    ws("WsOpen")
      .connect(url)
      .queryParam("token", s"$${$Token}")
      .queryParam("EIO", "3")
      .queryParam("transport", transport)
      .queryParam("user", s"$${$UserId}")

  def sendMessage(text: String, checkers: Seq[WsTextFrameCheck]) =
    ws("Send message")
      .sendText(text)
      .await(1 seconds) (checkers: _*)

  def closeConnection =
    ws("Ws close").close
}
