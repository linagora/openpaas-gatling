package com.linagora.openpaas.gatling.provisionning

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.provisionning.Authentication._
import com.linagora.openpaas.gatling.utils.RandomStringGenerator.randomString

object RandomFeeder {

  type UserFeeder = Array[Map[String, String]]

  def toFeeder(userCount: Int): UserFeeder =
    (0 until userCount)
      .map(_ => Map(
        UsernameSessionParam -> s"$randomString@$DomainName",
        PasswordSessionParam -> randomString))
      .toArray
}
