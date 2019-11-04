package com.linagora.openpaas.gatling.utils

import io.gatling.core.Predef._

object Debug {
  def printSessionVariable(key: String) = exec(session => {
    println(s"$key = ${session(key).as[String]}")
    session
  })
}
