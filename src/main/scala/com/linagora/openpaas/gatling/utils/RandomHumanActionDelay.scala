package com.linagora.openpaas.gatling.utils

import com.linagora.openpaas.gatling.Configuration._
import scala.util.Random

object RandomHumanActionDelay {
  def humanActionDelay() : Int = {
    return HumanActionMinDelay + new Random().nextInt(HumanActionMaxDelay - HumanActionMinDelay + 1)
  }
}
