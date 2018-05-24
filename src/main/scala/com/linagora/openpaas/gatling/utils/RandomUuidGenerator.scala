package com.linagora.openpaas.gatling.utils

import java.util.UUID

object RandomUuidGenerator {
  def randomUuidString = UUID.randomUUID.toString
}
