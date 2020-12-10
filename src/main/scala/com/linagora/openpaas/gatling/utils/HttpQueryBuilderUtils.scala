package com.linagora.openpaas.gatling.utils

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.cookie.{CookieJar, CookieSupport}
import io.gatling.http.request.builder.HttpRequestBuilder

object HttpQueryBuilderUtils {
  private val CookieBackup = CookieSupport.CookieJarAttributeName + "_backup"

  def execWithoutCookie(request: HttpRequestBuilder): ChainBuilder = {
    exec(session => {
      session
        .set(CookieBackup, CookieSupport.cookieJar(session).get)
        .remove(CookieSupport.CookieJarAttributeName)}
    )
      .exec(request)
      .exec(exec(session => {
        session
          .set(CookieSupport.CookieJarAttributeName, session(CookieBackup).asOption[CookieJar].get)
          .remove(CookieBackup)}
      ))
  }

}
