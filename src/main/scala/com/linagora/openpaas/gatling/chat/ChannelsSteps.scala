package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.chat.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import com.linagora.openpaas.gatling.utils.RandomStringGenerator.randomString
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.util.Random


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
      .check(jsonPath("$[*]._id").findAll.saveAs(ChannelIds))

  def listChannelsForUser(): HttpRequestBuilder =
    withAuth(http("listUserChannels")
      .get("/chat/api/user/conversations"))
      .check(status in(200, 304))
      .check(jsonPath("$[*]._id").findAll.saveAs(SubscribedChannelIds))

  def getChannelDetails =
      withAuth(http("getChannelDetails")
        .get(s"/chat/api/conversations/$${$ChannelId}"))
        .check(status in(200, 304))

  def getChannelMembers =
      withAuth(http("getChannelMembers")
        .get(s"/api/collaborations/chat.conversation/$${$ChannelId}/members/"))
        .check(status in(200, 304))

  def addChannelMember =
      withAuth(http("addChannelMembers")
        .put(s"/api/collaborations/chat.conversation/$${$ChannelId}/members/$${$UserId}"))
        .check(status in(201, 204))

  def getChannelMessages =
      withAuth(http("getChannelMessages")
        .get(s"/chat/api/conversations/$${$ChannelId}/messages"))
        .check(status in(200, 304))

  def pickOneChannel =
    exec((session: Session) => session.set(ChannelId,
      Random.shuffle(session.get(ChannelIds).as[Vector[String]])
        .head))

  def createPrivateChannel(): HttpRequestBuilder =
    withAuth(http("createPrivateChannels")
      .post("/chat/api/conversations"))
      .body(StringBody(s"""
{
  "type": "directmessage",
  "domain": "$DomainId",
  "members": ["$${$OtherUserId}"],
  "mode": "channel"
}"""))
      .check(status is 201)
}
