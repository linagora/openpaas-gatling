package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.ContactSteps.provisionContacts
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInCollectedAddressBookScenari
import com.linagora.openpaas.gatling.calendar.EventSteps.provisionEvents
import com.linagora.openpaas.gatling.calendar.scenari.SearchEventsScenari
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.authentication.pkce.PKCESteps
import com.linagora.openpaas.gatling.unifiedinbox.scenari.InboxScenari
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt

class PlatformTestSimulation extends Simulation {
  private val feeder = csv("users.csv")

  val scn = scenario("Testing OpenPaaS mix scenarios")
    .feed(feeder.circular())
    .pause(1 second)
    .exec(login)
    .exec(getProfile())
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
        33.3 -> group("INBOX")(
          exec(InboxScenari.userLogin())
            .exec(repeat(20) {
              exec(PKCESteps.renewAccessToken)
                .randomSwitch(
                  10.0 -> exec(InboxScenari.generateOnceAlreadyLogged()),
                  90.0 -> exec(InboxScenari.idle())
                )
            }.exec(InboxScenari.userLogout()))),
        33.3 -> exec(OpenContactInCollectedAddressBookScenari.generate())
      ).pause(7500 milliseconds, 15 seconds)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
