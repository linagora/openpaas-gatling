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
package com.linagora.openpaas.gatling.core

import com.linagora.openpaas.gatling.core.UsersSteps.statusCode
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import com.linagora.openpaas.gatling.provisionning.Authentication._
import com.linagora.openpaas.gatling.provisionning.SessionKeys.UsernameSessionParam
import io.gatling.core.structure.ChainBuilder

import scala.concurrent.duration.DurationInt

object PeopleSteps {
  val avatarsToLoadAfterSearch = "avatarsToLoad"

  def search(queryKey: String): HttpRequestBuilder = {
    withAuth(
      http("Search people for autocomplete")
        .post(s"/api/people/search"))
      .body(
        StringBody(s"""{"q":"$${$queryKey}","objectTypes":["user","contact","group","ldap"],"limit":20,"excludes":[]}""".stripMargin))
      .check(status.in(200, 304).saveAs(statusCode),
        jsonPath("$[*].emailAddresses[0]").findAll.saveAs(avatarsToLoadAfterSearch)
      )
  }

  def simulatePeopleSearch(): ChainBuilder = {
    group("simulatePeopleSearch") {
      exec(session => session.set("userNameFirstLetter", session(UsernameSessionParam).as[String].substring(0, 1)))
        .exec(session => session.set("userNameFirst3Letters", session(UsernameSessionParam).as[String].substring(0, 3)))
        .exec(PeopleSteps.search("userNameFirstLetter"))
        .exec(session => AvatarsSteps.extractRandomAvatar(session, "avatarToLoadA"))
        .exec(session => AvatarsSteps.extractRandomAvatar(session, "avatarToLoadB"))
        .exec(session => AvatarsSteps.extractRandomAvatar(session, "avatarToLoadC"))
        .exec(AvatarsSteps.search("avatarToLoadA"))
        .exec(AvatarsSteps.search("avatarToLoadB"))
        .exec(AvatarsSteps.search("avatarToLoadC"))
        .pause(1 second)
        .exec(PeopleSteps.search("userNameFirst3Letters"))
        .exec(session => AvatarsSteps.extractRandomAvatar(session, "avatarToLoadSecondQueryA"))
        .exec(session => AvatarsSteps.extractRandomAvatar(session, "avatarToLoadSecondQueryB"))
        .exec(AvatarsSteps.search("avatarToLoadSecondQueryA"))
        .exec(AvatarsSteps.search("avatarToLoadSecondQueryB"))
        .pause(1 second)
        .exec(PeopleSteps.search(UsernameSessionParam))
        .exec(session => AvatarsSteps.extractRandomAvatar(session, "avatarToLoadThirdQuery"))
        .exec(AvatarsSteps.search("avatarToLoadThirdQuery"))
    }
  }
}
