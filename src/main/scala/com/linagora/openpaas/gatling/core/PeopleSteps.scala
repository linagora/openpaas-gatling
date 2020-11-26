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
import com.linagora.openpaas.gatling.provisionning.Authentication._
import io.gatling.http.request.builder.HttpRequestBuilder

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

}
