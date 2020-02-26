package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.AddressBooksSteps._
import com.linagora.openpaas.gatling.core.DomainSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import io.gatling.core.Predef._
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactScenari
import com.linagora.openpaas.gatling.calendar.CalendarsSteps.provisionEvents
import com.linagora.openpaas.gatling.calendar.scenari.SearchEventsScenari
import com.linagora.openpaas.gatling.chat.scenari.SendMessageScenari
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps.provisionMessages
import com.linagora.openpaas.gatling.unifiedinbox.scenari.SendEmailScenari

import scala.concurrent.duration.DurationInt

class OpenPaaSMixSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS mix scenarios")
    .exec(createGatlingTestDomainIfNotExist)
    .pause(1 second)
    .feed(feeder.circular())
    .pause(1 second)
    .exec(login)
    .exec(getProfile())
    .pause(1 second)
    .exec(provisionMessages)
    .pause(1 second)
    .exec(provisionEvents)
    .pause(1 second)
    .exec(provisionContacts)
    .pause(1 second)
    .exec(logout)
    .pause(1 second)
    .during(ScenarioDuration) {
      randomSwitch(
        33.3 -> exec(SearchEventsScenari.generate()),
        33.3 -> exec(SendEmailScenari.generate()),
        33.3 -> exec(OpenContactScenari.generate())
      ).pause(7500 milliseconds, 15 seconds)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(httpProtocol)
}
