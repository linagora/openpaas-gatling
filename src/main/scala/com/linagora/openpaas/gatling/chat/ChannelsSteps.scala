package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.utils.RandomStringGenerator.randomString
import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object ChannelsSteps {
  def createChannel(): HttpRequestBuilder =
    withAuth(http("createChannel")
      .post("/chat/api/conversations"))
      .body(StringBody(s"""
{
  "type": "open",
  "domain": "$DomainId",
  "name": "$randomString",
  "mode": "channel"
}"""))
      .check(status is 201)

  def listChannels(): HttpRequestBuilder =
    withAuth(http("listChannels")
      .get("/chat/api/conversations"))
      .check(status in(200, 304))

  def listChannelsForUser(): HttpRequestBuilder =
    withAuth(http("listUserChannels")
      .get("/chat/api/user/conversations"))
      .check(status in(200, 304))
}
