package com.linagora.openpaas.gatling.utils

import com.linagora.openpaas.gatling.provisionning.SessionKeys.{OtherUsername, UsernameSessionParam}
import io.gatling.core.Predef._
import io.gatling.core.feeder.Feeder
import io.gatling.core.structure.ChainBuilder

object OtherUserSelector {
  def selectFrom(userFeeder: Feeder[Any]): ChainBuilder =
    exec((session: Session) => session.set(
      OtherUsername,
      userFeeder
        .map(record => record ("username"))
        .filter(username => username != session(UsernameSessionParam).as[String])
        .next()))
}
