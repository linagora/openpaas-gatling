package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.unifiedinbox.LoginTemplateRequestsList._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object TemplatesSteps {
  def loadOpeningEventTemplates: ChainBuilder =
    group("Load templates when redirecting to unifiedinbox page after login") {
      repeat(redirectToUnifiedInboxPageAfterLogin.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = redirectToUnifiedInboxPageAfterLogin(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in(200, 304)))
      }
    }

  def loadOpeningComposerTemplates: ChainBuilder =
    group("Load templates when opening composer") {
      repeat(openingComposerTemplates.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = openingComposerTemplates(index)
          session.set("resourceURL", resourceURL)
        })
          .exec(http(s"Load $${resourceURL}")
            .get(s"$${resourceURL}")
            .check(status in(200, 304)))
      }
    }
}
