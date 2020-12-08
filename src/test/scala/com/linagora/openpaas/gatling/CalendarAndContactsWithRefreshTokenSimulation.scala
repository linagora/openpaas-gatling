package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.AddressBookSteps
import com.linagora.openpaas.gatling.addressbook.SessionKeys.ContactUuid
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInDefaultAddressBookScenari
import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.calendar.scenari._
import com.linagora.openpaas.gatling.core.{LoginSteps, TokenSteps}
import com.linagora.openpaas.gatling.core.WebSocketSteps
import com.linagora.openpaas.gatling.core.authentication.pkce.PKCESteps
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

class CalendarAndContactsWithRefreshTokenSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val eventUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))
  val contactUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map(ContactUuid -> randomUuidString))
  val scn: ScenarioBuilder = scenario("CalendarAndContactsPlatformTestScenario")
    .feed(userFeeder)
    .randomSwitch(
      80.0 -> {
        exec(CalendarSteps.openCalendarSPA())
          .during(ScenarioDuration) {
            exec(PKCESteps.renewAccessToken)
              .exec(TokenSteps.retrieveAuthenticationToken)
              .randomSwitch(
                20.0 -> exec(CalendarMixScenari.generate(eventUuidFeeder)),
                80.0 -> exec(CalendarSteps.idle())
              )
          }
          .pause(RandomHumanActionDelay.humanActionDelay())
          .exec(WebSocketSteps.closeWsConnection)
          .exec(LoginSteps.logout)
      },
      20.0 -> {
        exec(AddressBookSteps.openContactsSpa())
          .during(ScenarioDuration) {
            exec(PKCESteps.renewAccessToken)
              .randomSwitch(
                10.0 -> exec(OpenContactInDefaultAddressBookScenari.generate(contactUuidFeeder)),
                90.0 -> exec(AddressBookSteps.idle())
              )
          }
          .pause(RandomHumanActionDelay.humanActionDelay())
          .exec(WebSocketSteps.closeWsConnection)
          .exec(LoginSteps.logout)
      }
    )

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
