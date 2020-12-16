package com.linagora.openpaas.gatling.calendarAndContact.scenari

import com.linagora.openpaas.gatling.Configuration.ScenarioDuration
import com.linagora.openpaas.gatling.addressbook.AddressBookSteps
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInDefaultAddressBookScenari
import com.linagora.openpaas.gatling.calendar.CalendarSteps
import com.linagora.openpaas.gatling.calendar.scenari.CalendarMixScenari
import com.linagora.openpaas.gatling.core.WebSocketSteps
import com.linagora.openpaas.gatling.core.{LoginSteps, TokenSteps}
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

object CalendarAndContactsScenari {
  def generate(eventUuidFeeder: Iterator[Map[String, String]], contactUuidFeeder: Iterator[Map[String, String]], userFeeder: SourceFeederBuilder[String]): ScenarioBuilder = {
    scenario("CalendarAndContactsPlatformTestScenario")
      .feed(userFeeder.circular)
      .randomSwitch(
        80.0 -> {
          exec(CalendarSteps.openCalendarSPA())
            .during(ScenarioDuration) {
                randomSwitch(
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
                randomSwitch(
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
