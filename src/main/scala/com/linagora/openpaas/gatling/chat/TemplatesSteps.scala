package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.chat.ChatTemplateRequestsList._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object TemplatesSteps {
  def loadTemplatesWhenRedirectingToChatPageAfterLogin: ChainBuilder =
    group("Load templates when redirecting to chat page after login") {
      repeat(redirectToChatPageAfterLogin.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = redirectToChatPageAfterLogin(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in(200, 304)))
      }
    }
}
