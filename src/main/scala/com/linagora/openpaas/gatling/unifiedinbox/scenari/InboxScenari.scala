package com.linagora.openpaas.gatling.unifiedinbox.scenari

import com.linagora.openpaas.gatling.Configuration
import com.linagora.openpaas.gatling.Configuration.ScenarioDuration
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
      .exec(login)
      .exec(getProfile())
      .exec(logout)
      .during(ScenarioDuration) {
        group("INBOX")(
          exec(InboxScenari.userLogin())
            .exec(repeat(20) {
                randomSwitch(
                  10.0 -> exec(InboxScenari.generateOnceAlreadyLogged()),
                  90.0 -> exec(InboxScenari.idle()) // I think there is a misconception here...
                  // idle should mean inbox open but no input... which is only a getMessagesList + getMessages now with after param placed on most recent message
                )
            }.exec(InboxScenari.userLogout()))
        ).pause(7500 milliseconds, 15 seconds)
      }

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

  def generateOnceAlreadyLogged() = group("send")(
    exec(generateJwtTokenWithAuth)
      .pause(humanActionDelay() second)
      .exec(getMailboxes)
      .exec(getMessageList)
      // TODO: missing getMessages???
      .repeat(10)(exec(AvatarsSteps.search(UsernameSessionParam, withRandomDisplayName = true)))
      .pause(humanActionDelay() second)
      .group("do send email")(sendEmailSteps))
  // it feels like the user here is checking an other mailbox before sending a mail...
  // we should separate that into two distinct steps and add it into the random switch of the scenario


  def userLogin() = group("login")(
    exec(loadLoginTemplates)
      .exec(login)
      .exec(StaticAssetsSteps.loadIndexHtml(Configuration.InboxSpaPath))
      .exec(openWsConnection())
      //.exec(loadOpeningEventTemplates) // static assets delivered by nginx
      .exec(generateJwtTokenWithAuth)
      .exec(getVacationResponse)
      .exec(getMailboxes)
      .exec(getMessageList))
      // TODO: missing getMessages??? and maybe get some avatars as well???

  def userLogout() = group("logout")(exec(closeWsConnection)
    .pause(humanActionDelay() second)
    .exec(logout))

  def idle() = group("idle")(
    exec(getMailboxes) // that's not what I see
      .exec(getMessageList) // when idle we only fetch the messages from the date of the most recent one... not the all list, should change
      // TODO: missing getMessages???
      .repeat(3)(exec(AvatarsSteps.search(UsernameSessionParam, withRandomDisplayName = true))) // if user is idle, the hell is that search coming from???
      .pause(60.seconds))

  private def sendEmailSteps = {
    //exec(loadOpeningComposerTemplates) // static assets delivered by nginx
    pause(humanActionDelay() second)
      .exec(PeopleSteps.simulatePeopleSearch())
      .pause(humanActionDelay() second)
      .exec(uploadAttachment)
      .pause(humanActionDelay() second)
      .exec(sendMessageWithAttachment)
  }
}
