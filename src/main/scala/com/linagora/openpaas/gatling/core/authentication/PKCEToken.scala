/** **************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0                 *
 * *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 * ************************************************************** */
package com.linagora.openpaas.gatling.core.authentication

import com.google.common.base.Charsets
import com.linagora.openpaas.gatling.Configuration.{KeycloakPortalUrl, KeycloakRealm, LemonLDAPPortalUrl, OidcCallback, OidcClient}
import com.linagora.openpaas.gatling.utils.HttpQueryBuilderUtils
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import java.net.URLEncoder

object PKCEToken {

  def getToken: ChainBuilder = HttpQueryBuilderUtils.execWithoutCookie(
    http("get token")
      .post(KeycloakPortalUrl + s"/auth/realms/${KeycloakRealm}/protocol/openid-connect/token")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("client_id", OidcClient)
      .formParam("code", "${authorization_code}")
      .formParam("redirect_uri", s"${URLEncoder.encode(OidcCallback, Charsets.UTF_8)}")
      .formParam("code_verifier", "${pkce_code_verifier}")
      .formParam("grant_type", "authorization_code")
      .check(status.is(200),
        jsonPath("$.access_token").find.saveAs("access_token"),
        jsonPath("$.refresh_token").find.saveAs("refresh_token"),
        jsonPath("$.id_token").find.saveAs("id_token"),
        jsonPath("$.expires_in").find.saveAs("expires_in")
      ))
    .exec(session => session.set("token_acquisition_time", timeInMillis().toString))

  def renewTokenIfNeeded: ChainBuilder =
    doIf(session => {
      val expiresInSeconds: Int = session("expires_in").validate[Int].toOption.getOrElse(3601)
      val tokenAcquisitionTime = session("token_acquisition_time").validate[Long].toOption
      val lastRenewTime: Option[Long] = session("last_renew").validate[Long].toOption
      lastRenewTime.orElse(tokenAcquisitionTime) match {
        case None => false //token never acquired should not happen
        case Some(last) if timeInMillis() >= (last + expiresInSeconds * 1000) => true
        case Some(_) => false
      }
    })(exec(doRenewAccessToken)
      .exec(session => session.set("last_renew", timeInMillis())))

  private def doRenewAccessToken: HttpRequestBuilder =
    http("refresh token")
      .post(KeycloakPortalUrl + s"/auth/realms/${KeycloakRealm}/protocol/openid-connect/token")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("client_id", OidcClient)
      .formParam("refresh_token", "${refresh_token}")
      .formParam("grant_type", "refresh_token")
      .check(status.is(200),
        jsonPath("$.access_token").find.saveAs("access_token")
      )

  private def timeInMillis(): Long = System.nanoTime() / 1000000
}
