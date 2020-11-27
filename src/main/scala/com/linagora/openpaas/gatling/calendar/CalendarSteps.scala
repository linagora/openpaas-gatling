package com.linagora.openpaas.gatling.calendar

import com.linagora.openpaas.gatling.Configuration.{CalendarCount, CalendarSpaPath, SabreBaseUrl}
import com.linagora.openpaas.gatling.calendar.CalendarConstants.ESNToken
import com.linagora.openpaas.gatling.utils.RandomUuidGenerator.randomUuidString
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import com.linagora.openpaas.gatling.calendar.SessionKeys._
import com.linagora.openpaas.gatling.calendar.utils.CalendarUtils
import com.linagora.openpaas.gatling.core.{DomainSteps, LoginSteps, StaticAssetsSteps, TokenSteps, UserSteps, WebSocketSteps}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.structure.ChainBuilder

import scala.concurrent.duration.DurationInt
import scala.util.matching.Regex

object CalendarSteps {
  def openCalendarSPA(): ChainBuilder = {
    group("openCalendarSPA") {
      exec(LoginSteps.loadLoginTemplates)
        .exec(LoginSteps.login())
        .exec(StaticAssetsSteps.loadIndexHtmlAndMainJs(CalendarSpaPath))
        .exec(StaticAssetsSteps.loadStaticAssets(CalendarStaticAssets.OpeningCalendarAssets))
        .exec(TokenSteps.retrieveAuthenticationToken)
        .exec(WebSocketSteps.getSocketId)
        .exec(WebSocketSteps.registerSocketNamespaces)
        .exec(WebSocketSteps.openWsConnection())
        .exec(UserSteps.getProfile())
        .exec(DomainSteps.getDomain)
        .exec(DomainSteps.getThemeForDomain())
        .exec(DomainSteps.getLogoForDomain)
        .exec(CalendarSteps.getCalendarConfiguration())
        .exec(CalendarSteps.listUsableCalendarsForUser())
        .exec(EventSteps.listEvents())
    }
  }

  def createCalendar(): HttpRequestBuilder = {
    http("createCalendar")
      .post(s"$SabreBaseUrl/calendars/$${$UserId}")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody(session => {
        val calId: String = randomUuidString

        s"""
        {
          "id":"$calId",
          "dav:name":"Test calendar - $calId",
          "apple:color":"#01ea18",
          "caldav:description":""
        }
        """
      }))
      .check(status is 201)
  }

  def deleteCalendar(): HttpRequestBuilder = {
    http("deleteCalendar")
      .delete(s"$SabreBaseUrl$${$CalendarLink}")
      .header(ESNToken, s"$${$Token}")
      .check(status is 204)
  }

  def updateCalendar(): HttpRequestBuilder = {
    http("updateCalendar")
      .httpRequest("PROPPATCH", s"$SabreBaseUrl$${$CalendarLink}")
      .header(ESNToken, s"$${$Token}")
      .body(StringBody(s"$${$NewCalendarContent}"))
      .check(status is 204)
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

  def listCalendarsForUser(): HttpRequestBuilder =
    http("listCalendars")
      .get(s"$SabreBaseUrl/calendars/$${$UserId}.json")
      .header(ESNToken, s"$${$Token}")
      .check(status in(200, 304))

  def listUsableCalendarsForUser(): HttpRequestBuilder =
    http("listUsableCalendars")
      .get(s"$SabreBaseUrl/calendars/$${$UserId}.json?personal=true&sharedDelegationStatus=accepted&sharedPublicSubscription=true&withRights=true")
      .header(ESNToken, s"$${$Token}")
      .check(status in(200, 304))
      .check(jsonPath("$._embedded['dav:calendar'][*]._links.self.href").saveAs(CalendarLinks))
      .check(jsonPath("$._embedded['dav:calendar'][0]._links.self.href").saveAs(CalendarLink))

  def getSecondCalendar(): HttpRequestBuilder =
    listUsableCalendarsForUser()
      .check(jsonPath("$._embedded['dav:calendar'][1]._links.self.href").exists)
      .check(jsonPath("$._embedded['dav:calendar'][1]._links.self.href").saveAs(CalendarLink))

  def getCalendarByCalendarIdInSession(): HttpRequestBuilder =
    http("getDefaultCalendar")
      .get(s"$SabreBaseUrl/calendars/$${$UserId}/$${$CalendarId}.json?withRights=true")
      .header(ESNToken, s"$${$Token}")
      .check(jsonPath("$").saveAs(CalendarContent))
      .check(jsonPath("$._links.self.href").saveAs(CalendarLink))
      .check(status is 200)

  def getDefaultCalendar(): HttpRequestBuilder =
    http("getDefaultCalendar")
      .get(s"$SabreBaseUrl/calendars/$${$UserId}/$${$UserId}.json?withRights=true")
      .header(ESNToken, s"$${$Token}")
      .check(jsonPath("$").saveAs(CalendarContent))
      .check(jsonPath("$._links.self.href").saveAs(CalendarLink))
      .check(status is 200)

  def getCalendarConfiguration(): HttpRequestBuilder =
    http("getCalendarConfiguration")
      .post("/api/configurations?scope=user")
      .body(StringBody("""[{"name":"linagora.esn.calendar","keys":["workingDays","hideDeclinedEvents"]}]"""))
      .check(status in(200, 304))

  def provisionCalendars(): ChainBuilder = {
    group("provisionCalendars") {
      repeat(CalendarCount) {
        exec(createCalendar())
          .pause(1 second)
      }
    }
  }
}
