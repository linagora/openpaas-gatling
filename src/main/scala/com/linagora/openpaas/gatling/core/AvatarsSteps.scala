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

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.util.Random

object AvatarsSteps {
  def search(queryKey: String, withRandomDisplayName: Boolean = false): HttpRequestBuilder = {
    val query = http("load avatar for user" + (if(withRandomDisplayName) " with random display name" else ""))
      .get(s"/api/avatars")
      .queryParam("objectType", "user")
      .queryParam("email", s"$${$queryKey}")

    (if (withRandomDisplayName) {
      query.queryParam("displayName", _ => Random.alphanumeric.filter(_.isLetter).take(Random.nextInt(10) + 1).toList.mkString
      )
    }
    else {
      query
    })
      .check(status.in(200, 304))
  }
}
