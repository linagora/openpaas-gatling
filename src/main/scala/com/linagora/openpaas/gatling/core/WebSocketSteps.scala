package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.provisionning.SessionKeys.{Token, UserId}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import play.api.libs.json.Json

object WebSocketSteps {
  def getSocketId =
    exec(
      http("get sid")
      .get("/socket.io/")
      .queryParam("token", s"$${$Token}")
      .queryParam("EIO", "3")
      .queryParam("transport", "polling")
      .queryParam("user", s"$${$UserId}")
      .check(status in (200, 304))
      .check(regex("""\{(.*?)\}""").saveAs("socketResponse"))
    )
    .exec(session => {
      // format follow this: https://socket.io/docs/internals/#Dependency-graph
      // "sid":"bsLUkfV5MyJHGkC4AAAb","upgrades":["websocket"],"pingInterval":25000,"pingTimeout":5000
      val socketResponse = s"{${session("socketResponse").as[String]}}"

      implicit val modelFormat = Json.format[Socket]
      val parsed = Json.parse(socketResponse).as[Socket]

      session.set("sid", parsed.sid)
    })

  def registerSocketNamespaces =
    http("get sid")
      .post("/socket.io/")
      .queryParam("token", s"$${$Token}")
      .queryParam("EIO", "3")
      .queryParam("transport", "polling")
      .queryParam("user", s"$${$UserId}")
      .check(status is 200)
      .queryParam("sid", "${sid}")
      .body(StringBody("""17:40/collaboration,15:40/graceperiod,13:40/calendars,14:40/userstatus,18:40/contact-import,19:40/videoconference,8:40/chat,17:40/notifications,"""))

  def openWsConnection(url: String = "/", transport: String = "websocket") =
    ws("WsOpen")
      .connect(url)
      .queryParam("token", s"$${$Token}")
      .queryParam("EIO", "3")
      .queryParam("transport", transport)
      .queryParam("user", s"$${$UserId}")
    .queryParam("sid", "${sid}")

  def closeWsConnection =
    ws("Ws close").close
}

case class Socket(sid: String, upgrades: Array[String], pingInterval: Int, pingTimeout: Int)

