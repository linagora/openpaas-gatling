package com.linagora.openpaas.gatling.provisionning

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.utils.RandomStringGenerator.randomString
import io.gatling.core.Predef._

import scala.util.Random

class RandomFeeder(userCount: Int) {
  type UserFeeder = Array[Map[String, String]]

  val userList: List[User] = (0 until userCount)
    .map(_ => User(Username(s"$randomString@${DomainName}"), Password(randomString)))
    .toList

  def asFeeder(): UserFeeder =
    userList.map(user => Map(
      UsernameSessionParam -> user.username.username,
      PasswordSessionParam -> user.password.password))
      .toArray

  def selectUsernameStep() =
    exec((session: Session) => session.set(SessionKeys.OtherUsername, Random.shuffle(userList).head.username.username))
}

case class Username(username: String)
case class Password(password: String)
case class User(username: Username, password: Password)