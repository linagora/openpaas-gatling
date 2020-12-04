package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.AddressBookSteps
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInCollectedAddressBookScenari
import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.calendar.scenari._
import com.linagora.openpaas.gatling.core.{LoginSteps, TokenSteps}
import com.linagora.openpaas.gatling.core.LoginSteps.{login, logout}
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.WebSocketSteps.closeWsConnection
import com.linagora.openpaas.gatling.core.authentication.pkce.PKCESteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

class CalendarAndContactsWithRefreshTokenSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val eventUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))
  val scn: ScenarioBuilder = scenario("CalendarAndContactsPlatformTestScenario")
    .feed(userFeeder)
    .during(ScenarioDuration) {
      randomSwitch(
        80.0 -> {
          exec(CalendarSteps.openCalendarSPA())
            .repeat(20) {
              exec(PKCESteps.renewAccessToken)
                .exec(TokenSteps.retrieveAuthenticationToken)
                .randomSwitch(
                  20.0 -> exec(CalendarMixScenari.generate(eventUuidFeeder)),
                  80.0 -> exec(CalendarSteps.idle())
                )
            }
            .pause(RandomHumanActionDelay.humanActionDelay())
            .exec(closeWsConnection)
            .exec(LoginSteps.logout)
        },
        20.0 -> {
          exec(AddressBookSteps.openContactsSpa())
            .repeat(20) {
              exec(PKCESteps.renewAccessToken)
                .randomSwitch(
                  10.0 -> exec(OpenContactInCollectedAddressBookScenari.generate()),
                  90.0 -> exec(AddressBookSteps.idle())
                )
            }
            .pause(RandomHumanActionDelay.humanActionDelay())
            .exec(closeWsConnection)
            .exec(LoginSteps.logout)
        }
      ).pause(5 seconds, 10 seconds)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
