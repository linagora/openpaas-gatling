package com.linagora.openpaas.gatling.core

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
      .check(status is 200)
      .check(bodyString.saveAs(IndexHtmlContent))
  }

  def loadMainJs(): HttpRequestBuilder = {
    http("loadMainJs")
      .get(s"$${$MainJsUrl}")
      .check(status is 200)
  }

  def loadIndexHtmlAndMainJs(spaName: String): ChainBuilder = {
    group("loadIndexHtmlAndMainJs") {
      exec(loadIndexHtml(spaName))
        .exec(session => extractMainJsUrl(spaName)(session))
        .exec(loadMainJs())
    }
  }

  def extractMainJsUrl(spaName: String)(session: Session): Session = {
    val indexHtmlContent: String = session(IndexHtmlContent).as[String]
    val matchMainJsUrlRegex: Regex = raw""""\/${spaName.toLowerCase()}\/main(.*?)"""".r
    val mainJsUrl: String = matchMainJsUrlRegex.findFirstIn(indexHtmlContent).mkString.drop(1).dropRight(1)

    session.set(MainJsUrl, mainJsUrl)
  }
}
