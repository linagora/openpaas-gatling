package com.linagora.openpaas.gatling.utils

import com.linagora.openpaas.gatling.Configuration._
import io.gatling.commons.validation.Success
import io.gatling.core.session.Expression

import scala.concurrent.duration._
import scala.util.Random

object RandomHumanActionDelay {
  def humanActionDelay() : Expression[FiniteDuration] = {
    _ => Success((HumanActionMinDelay + new Random().nextInt(HumanActionMaxDelay - HumanActionMinDelay + 1)).seconds)
  }
}
