package com.linagora.openpaas.gatling

import com.linagora.gatling.imap.PreDef.imap
import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.calendarAndContact.scenari.CalendarAndContactsScenari
import com.linagora.openpaas.gatling.unifiedinbox.scenari.InboxScenari
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import org.apache.james.gatling.control.AuthenticatedUserFeeder.AuthenticatedUserFeederBuilder
import org.apache.james.gatling.control.{Password, RecipientFeeder, User, UserFeeder, Username}
import org.apache.james.gatling.imap.scenari.{PlatformValidationScenario => ImapPlatformValidationScenario}
import org.apache.james.gatling.jmap.draft.scenari.{PlatformValidationScenario => JmapPlatformValidationScenario}
import org.apache.james.gatling.simulation.{Configuration => JamesConfiguration, HttpSettings}

class PlatformSimulation extends Simulation {
  private val inboxFeeder = csv("users.csv")
  val calendarFeeder: SourceFeederBuilder[String] = csv("users.csv").queue
  val eventUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))
  val contactUuidFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("eventUuid" -> randomUuidString))

  private def recordValueToString(recordValue: Any): String = recordValue match {
    case s: String => s
    case a: Any => println("Warning: calling toString on a feeder value"); a.toString
  }

  val authenticatedUsers: Seq[User] = csv("users.csv").readRecords
    .map(record =>
      User(
        username = Username(recordValueToString(record("username"))),
        password = Password(recordValueToString(record("password")))))

  val jamesFeeder: AuthenticatedUserFeederBuilder = UserFeeder.toFeeder(authenticatedUsers)


  val inbox = InboxScenari.platform(inboxFeeder)

  val calendarAndContacts = CalendarAndContactsScenari.generate(eventUuidFeeder, contactUuidFeeder, userFeeder = calendarFeeder)

  val jmapJames = new JmapPlatformValidationScenario(minMessagesInMailbox = 10)
    .generate(duration = ScenarioDuration, userFeeder = jamesFeeder, recipientFeeder = RecipientFeeder.usersToFeeder(authenticatedUsers))

  val imapJames = new ImapPlatformValidationScenario()
    .generate(duration = ScenarioDuration, userFeeder = jamesFeeder)

  setUp(
    inbox.inject(rampUsers(UserCount) during(InjectDuration)).protocols(HttpProtocol),
    calendarAndContacts.inject(rampUsers(UserCount) during(InjectDuration)).protocols(HttpProtocol),
    jmapJames.inject(rampUsers(UserCount) during InjectDuration).protocols(HttpSettings.httpProtocol),
    imapJames.inject(rampUsers(UserCount) during InjectDuration).protocols(HttpSettings.httpProtocol, imap.host(JamesConfiguration.ImapServerHostName).build())
  )
}
