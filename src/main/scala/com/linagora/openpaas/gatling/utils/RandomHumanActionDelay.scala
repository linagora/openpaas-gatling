package com.linagora.openpaas.gatling.utils

import com.linagora.openpaas.gatling.Configuration._
import scala.util.Random

object RandomHumanActionDelay {
  def humanActionDelay() : Int = {
    return humanActionMinDelay + new Random().nextInt(humanActionMaxDelay - humanActionMinDelay + 1) 
  }
}
