package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration.{CalendarCount, CalendarSpaPath, SabreBaseUrl}
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.calendar.utils.CalendarUtils
import com.linagora.openpaas.gatling.calendar.utils.CalendarUtils.getCalDAVBaseUrl
import com.linagora.openpaas.gatling.core.TokenSteps.withTokenAuth
import com.linagora.openpaas.gatling.core.{DomainSteps, LoginSteps, StaticAssetsSteps, TokenSteps, UserSteps, WebSocketSteps}
import com.linagora.openpaas.gatling.provisionning.Authentication.withAuth
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder

import scala.concurrent.duration.DurationInt
import scala.util.matching.Regex

object CalendarSteps {
  def openCalendarSPA(): ChainBuilder = {
    group("openCalendarSPA") {
      exec(LoginSteps.loadLoginTemplates)
        .exec(LoginSteps.login(CalendarSpaPath))
        .exec(StaticAssetsSteps.loadIndexHtmlAndMainJs(CalendarSpaPath))
        .exec(StaticAssetsSteps.loadStaticAssets(CalendarStaticAssets.OpeningCalendarAssets))
        .exec(UserSteps.getProfile())
        .exec(WebSocketSteps.openWsConnection())
        .exec(DomainSteps.getDomain)
        .exec(DomainSteps.getThemeForDomain())
        .exec(DomainSteps.getLogoForDomain)
        .exec(CalendarSteps.getCalendarConfiguration())
        .exec(CalendarSteps.listUsableCalendarsForUser())
        .exec(EventSteps.listEvents())
    }
  }

  def createCalendar(): ChainBuilder = withTokenAuth(http("createCalendar")
    .post(s"${getCalDAVBaseUrl()}/calendars/$${$UserId}")
    .header("Accept", "application/json, text/plain, */*")
    .body(StringBody(session => {
      val calId: String = randomUuidString

      s"""{
        "id":"$calId",
        "dav:name":"Test calendar - $calId",
        "apple:color":"#01ea18",
        "caldav:description":""
      }
      """
    }))
    .check(status is 201))

  def deleteCalendar(): ChainBuilder = {
    withTokenAuth(http("deleteCalendar")
      .delete(s"${getCalDAVBaseUrl()}$${$CalendarLink}")
      .check(status is 204))
  }

  def updateCalendar(): ChainBuilder = {
    withTokenAuth(http("updateCalendar")
      .httpRequest("PROPPATCH", s"$SabreBaseUrl$${$CalendarLink}")
      .header("Accept", "application/json, text/plain, */*")
      .body(StringBody(s"$${$NewCalendarContent}"))
      .check(status is 204))
  }

  def setCalendarIdFromCalendarLinkInSession(session: Session) = {
    val calendarId: String = CalendarUtils.getCalendarIdFromCalendarLink(session(CalendarLink).as[String])

    session.set(CalendarId, calendarId)
  }

  def updateCalendarInSession(session: Session): Session = {
    val calendarId: String = session(CalendarId).as[String]
    val oldCalendarContent: String = session(CalendarContent).as[String]
    val matchLinksRegex: Regex = raw"""(\,"_links"\:\{(.*?)\})|("_links"\:\{(.*?)\}\}\,)""".r
    val matchDavNameRegex: Regex = raw""""dav\:name"\:"(.*?)"""".r
    val newCalendarContent: String = matchLinksRegex.replaceFirstIn(
      matchDavNameRegex.replaceFirstIn(oldCalendarContent, "\"dav:name\":\"[UPDATED] My calendar\""), ""
    ).patch(1, s""""id":"$calendarId",""", 0)

    session.set(NewCalendarContent, newCalendarContent)
  }

  def listCalendarsForUser(): ChainBuilder =
    withTokenAuth(http("listCalendars")
      .get(s"${getCalDAVBaseUrl()}/calendars/$${$UserId}.json")
      .header("Accept", "application/json, text/plain, */*")
      .check(status in(200, 304)))

  def listUsableCalendarsForUser(): ChainBuilder = withTokenAuth(listUsableCalendarRequest)

  def getSecondCalendar(): ChainBuilder =
    withTokenAuth(listUsableCalendarRequest
      .check(jsonPath("$._embedded['dav:calendar'][1]._links.self.href").exists)
      .check(jsonPath("$._embedded['dav:calendar'][1]._links.self.href").saveAs(CalendarLink)))

  private val listUsableCalendarRequest = http("listUsableCalendars")
    .get(s"${getCalDAVBaseUrl()}/calendars/$${$UserId}.json?personal=true&sharedDelegationStatus=accepted&sharedPublicSubscription=true&withRights=true")
    .header("Accept", "application/json, text/plain, */*")
    .check(status in(200, 304))
    .check(jsonPath("$._embedded['dav:calendar'][*]._links.self.href").saveAs(CalendarLinks))
    .check(jsonPath("$._embedded['dav:calendar'][0]._links.self.href").saveAs(CalendarLink))

  def getCalendarByCalendarIdInSession(): ChainBuilder =
    withTokenAuth(http("getDefaultCalendar")
      .get(s"${getCalDAVBaseUrl()}/calendars/$${$UserId}/$${$CalendarId}.json?withRights=true")
      .header("Accept", "application/json, text/plain, */*")
      .header("Accept", "application/json")
      .check(jsonPath("$").saveAs(CalendarContent))
      .check(jsonPath("$._links.self.href").saveAs(CalendarLink))
      .check(status is 200))

  def getDefaultCalendar(): ChainBuilder =
    withTokenAuth(http("getDefaultCalendar")
      .get(s"${getCalDAVBaseUrl()}/calendars/$${$UserId}/$${$UserId}.json?withRights=true")
      .header("Accept", "application/json, text/plain, */*")
      .check(jsonPath("$").saveAs(CalendarContent))
      .check(jsonPath("$._links.self.href").saveAs(CalendarLink))
      .check(status is 200))

  def getCalendarConfiguration(): ChainBuilder =
    withAuth(http("getCalendarConfiguration")
      .post("/api/configurations?scope=user")
      .body(StringBody("""[{"name":"linagora.esn.calendar","keys":["workingDays","hideDeclinedEvents"]}]"""))
      .check(status in(200, 304)))

  def idle(): ChainBuilder =
    pause(30 seconds, 60 seconds)

  def provisionCalendars(): ChainBuilder = {
    group("provisionCalendars") {
      repeat(CalendarCount) {
        exec(createCalendar())
          .pause(1 second)
      }
    }
  }
}
