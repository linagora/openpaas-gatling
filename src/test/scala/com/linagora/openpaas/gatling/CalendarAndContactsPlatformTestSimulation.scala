package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.addressbook.scenari.OpenContactInCollectedAddressBookScenari
import com.linagora.openpaas.gatling.calendar.scenari._
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import scala.concurrent.duration.DurationInt

class CalendarAndContactsPlatformTestSimulation extends Simulation {
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val eventUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))
  val scn: ScenarioBuilder = scenario("CalendarAndContactsPlatformTestScenario")
    .feed(userFeeder)
    .during(ScenarioDuration) {
      randomSwitch(
        80.0 -> exec(CalendarMixScenari.generate(eventUuidFeeder)),
        20.0 -> exec(OpenContactInCollectedAddressBookScenari.generate())
      ).pause(5 seconds, 10 seconds)
    }

  setUp(scn.inject(rampUsers(UserCount) during(InjectDuration))).protocols(HttpProtocol)
}
