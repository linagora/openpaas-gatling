package com.linagora.openpaas.gatling.chat

import com.linagora.openpaas.gatling.chat.SessionKeys._
import io.gatling.core.Predef._

object Utils {
  def getChannelIdByName(name: String) = {
    exec(session => {
      val subscribedChannelIds = session(SubscribedChannelIds).as[Vector[String]]
      val subscribedChannelNames = session(SubscribedChannelNames).as[Vector[String]]
      val index = subscribedChannelNames.indexOf(name)

      session.set(ChannelId, subscribedChannelIds(index))
    })
  }
}
