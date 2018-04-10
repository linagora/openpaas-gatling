package com.linagora.openpaas.gatling.utils

import java.util.UUID

object RandomStringGenerator {
  def randomString = UUID.randomUUID().toString
}
