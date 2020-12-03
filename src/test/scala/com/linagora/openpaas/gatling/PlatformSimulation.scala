package com.linagora.openpaas.gatling

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendarAndContact.scenari.CalendarAndContactsScenari
import com.linagora.openpaas.gatling.unifiedinbox.scenari.InboxScenari
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder

class PlatformSimulation extends Simulation {
  private val feeder = csv("users.csv")
  val userFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val eventUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))

  val inbox = InboxScenari.platform()
    .feed(feeder.circular())
  val calendarAndContacts = CalendarAndContactsScenari.generate(eventUuidFeeder)
    .feed(userFeeder)

  setUp(
    inbox.inject(rampUsers(UserCount) during(InjectDuration)).protocols(HttpProtocol),
    calendarAndContacts.inject(rampUsers(UserCount) during(InjectDuration)).protocols(HttpProtocol)
  )
}
