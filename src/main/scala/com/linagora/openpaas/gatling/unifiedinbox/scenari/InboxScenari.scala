package com.linagora.openpaas.gatling.unifiedinbox.scenari

import com.linagora.openpaas.gatling.Configuration
import com.linagora.openpaas.gatling.Configuration.{LoadStaticAssets, ScenarioDuration}
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.TokenSteps.generateJwtTokenWithAuth
import com.linagora.openpaas.gatling.core.UserSteps.getProfile
import com.linagora.openpaas.gatling.core.WebSocketSteps._
import com.linagora.openpaas.gatling.core.{AvatarsSteps, PeopleSteps, StaticAssetsSteps}
import com.linagora.openpaas.gatling.provisionning.SessionKeys.UsernameSessionParam
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps._
import com.linagora.openpaas.gatling.unifiedinbox.TemplatesSteps._
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay._
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder

import scala.concurrent.duration.DurationInt

object InboxScenari {

  def platform(feederBuilder: SourceFeederBuilder[String]) = scenario("Inbox platform scenario")
    .feed(feederBuilder.circular)
      .exec(InboxScenari.userLogin())
      .during(ScenarioDuration) {
        group("INBOX")(
          randomSwitch(
            5.0 -> exec(InboxScenari.sendEmail()),
            10.0 -> exec(InboxScenari.openMailbox()),
            15.0 -> exec(InboxScenari.readEmail()),
            70.0 -> exec(InboxScenari.idle())
          ).pause(60.seconds) // interval time in which we got jmap polling requests when idle
        )
      }
      .exec(InboxScenari.userLogout())

  def generateOnceWithLogin() =
    exec(loadLoginTemplates)
      .exec(login)
      .exec(StaticAssetsSteps.loadIndexHtmlAndMainJs(Configuration.InboxSpaPath))
      .exec(openWsConnection())
      .exec(loadOpeningEventTemplates)
      .exec(generateJwtTokenWithAuth)
      .exec(getVacationResponse)
      .exec(getMailboxes)
      .exec(getMailboxes)
      .pause(humanActionDelay() second)
      .exec(getVacationResponse)
      .exec(getMailboxes)
      .pause(humanActionDelay() second)
      .exec(getMessageList)
      .repeat(10)(exec(AvatarsSteps.search(UsernameSessionParam, withRandomDisplayName=true)))
      .pause(humanActionDelay() second)
      .group("send email")(sendEmailSteps)
      .exec(closeWsConnection)
      .pause(humanActionDelay() second)
      .exec(logout)

  def openMailbox() = group("open mailbox")(
    exec(getMessageList)
      .exec(getMessages())
      .repeat(5)(exec(AvatarsSteps.search(UsernameSessionParam, withRandomDisplayName = true))))

  def readEmail() = group("read email")(
    exec(readMessage())
      .randomSwitch(
        90.0 -> exec(markAsRead()), // if message was unread
        10.0 -> exec()
      )
  )

  def sendEmail() = group("do send email")(sendEmailSteps)


  def userLogin() = group("login")(
    doIfEquals(LoadStaticAssets, true) {
      exec(loadLoginTemplates)
    }
      .exec(login)
      .exec(getProfile())
      .exec(StaticAssetsSteps.loadIndexHtmlAndMainJs(Configuration.InboxSpaPath))
      .exec(openWsConnection())
      .doIfEquals(LoadStaticAssets, true) {
        exec(loadOpeningEventTemplates) // static assets delivered by nginx
      }
      .exec(generateJwtTokenWithAuth)
      .exec(getVacationResponse)
      .exec(getMailboxes)
      .exec(getMessageList)
      .exec(getMessages())
      .repeat(5)(exec(AvatarsSteps.search(UsernameSessionParam, withRandomDisplayName = true))))

  def userLogout() = group("logout")(exec(closeWsConnection)
    .pause(humanActionDelay() second)
    .exec(logout))

  def idle() = group("idle")(
    randomSwitch(
      10.0 -> exec(getMailboxes),
      90.0 -> exec())
      .exec(getIdleMessageList)
      .exec(getMessages))

  private def sendEmailSteps = {
    doIfEquals(LoadStaticAssets, true) {
      exec(loadOpeningComposerTemplates) // static assets delivered by nginx
    }.pause(humanActionDelay() second)
      .exec(PeopleSteps.simulatePeopleSearch())
      .pause(humanActionDelay() second)
      .exec(uploadAttachment)
      .pause(humanActionDelay() second)
      .exec(sendMessageWithAttachment)
  }
}
