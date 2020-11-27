package com.linagora.openpaas.gatling.utils

import scala.util.Random

object RandomNumber {
  def between(min: Int, max: Int): Int =
    min + Random.nextInt(max - min + 1 )
}
