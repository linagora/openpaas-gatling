package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.Configuration.LoadStaticAssets
import com.linagora.openpaas.gatling.core.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.util.matching.Regex

object StaticAssetsSteps {
  def loadIndexHtml(spaName: String): HttpRequestBuilder = {
    http("loadIndexHtml")
      .get(s"/${spaName.toLowerCase()}")
      .check(status in 200)
      .check(bodyString.saveAs(IndexHtmlContent))
  }

  def loadMainJs(): HttpRequestBuilder = {
    http("loadMainJs")
      .get(s"$${$MainJsUrl}")
      .check(status in (200, 304))
  }

  def loadIndexHtmlAndMainJs(spaName: String): ChainBuilder = {
    group("loadIndexHtmlAndMainJs") {
      exec(loadIndexHtml(spaName))
        .doIfEquals(LoadStaticAssets, true) {
          exec(session => extractMainJsUrl(spaName)(session))
            .exec(loadMainJs())
        }
    }
  }

  def loadStaticAssets(staticAssets: Array[String]): ChainBuilder = {
    group("loadStaticAssets") {
      repeat(staticAssets.length, "index") {
        exec(session => {
          val index = session("index").as[Int]
          val resourceURL = staticAssets(index)

          session.set("staticAssetUrl", resourceURL)
        })
          .exec(http(s"load $${staticAssetUrl}")
            .get(s"$${staticAssetUrl}")
            .check(status in(200, 304)))
      }
    }
  }

  def extractMainJsUrl(spaName: String)(session: Session): Session = {
    val indexHtmlContent: String = session(IndexHtmlContent).as[String]
    val matchMainJsUrlRegex: Regex = raw""""\/${spaName.toLowerCase()}\/?assets\/main(.*?)"""".r
    val mainJsUrl: String = matchMainJsUrlRegex.findFirstIn(indexHtmlContent).mkString.drop(1).dropRight(1)

    session.set(MainJsUrl, mainJsUrl)
  }
}
