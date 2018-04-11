package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.utils.RandomStringGenerator.randomString
import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.util.Random

object ChannelsSteps {

  val channelIds = "channelIds"
  val channelId = "channelId"
  val subscribedChannelIds = "subscribedChannelIds"

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
      .check(jsonPath("$[*]._id").findAll.saveAs(channelIds))

  def listChannelsForUser(): HttpRequestBuilder =
    withAuth(http("listUserChannels")
      .get("/chat/api/user/conversations"))
      .check(status in(200, 304))
      .check(jsonPath("$[*]._id").findAll.saveAs(subscribedChannelIds))

  def getChannelDetails =
      withAuth(http("getChannelDetails")
        .get(s"/chat/api/conversations/$${$channelId}"))
        .check(status in(200, 304))

  def pickOneChannel =
    exec((session: Session) => session.set(channelId,
      Random.shuffle(session.get(channelIds).as[Vector[String]])
        .head))

}
