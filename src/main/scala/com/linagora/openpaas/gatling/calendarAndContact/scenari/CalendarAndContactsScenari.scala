package com.linagora.openpaas.gatling.calendarAndContact.scenari

import com.linagora.openpaas.gatling.Configuration.ScenarioDuration
import com.linagora.openpaas.gatling.addressbook.AddressBookSteps
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInDefaultAddressBookScenari
import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.calendar.scenari.CalendarMixScenari
import com.linagora.openpaas.gatling.core.LoginSteps.{login, logout}
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.WebSocketSteps
import com.linagora.openpaas.gatling.core.authentication.pkce.PKCESteps
import com.linagora.openpaas.gatling.core.{LoginSteps, TokenSteps}
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration.DurationInt

object CalendarAndContactsScenari {
  def generate(eventUuidFeeder: Iterator[Map[String, String]], contactUuidFeeder: Iterator[Map[String, String]]): ScenarioBuilder = {
    scenario("CalendarAndContactsPlatformTestScenario")
      .exec(login)
      .exec(getProfile())
      .exec(logout)
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
  }
}
