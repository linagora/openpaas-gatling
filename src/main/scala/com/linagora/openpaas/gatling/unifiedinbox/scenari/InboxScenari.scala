package com.linagora.openpaas.gatling.unifiedinbox.scenari

import com.linagora.openpaas.gatling.Configuration
import com.linagora.openpaas.gatling.unifiedinbox.TemplatesSteps._
import com.linagora.openpaas.gatling.unifiedinbox.JmapSteps._
import com.linagora.openpaas.gatling.core.LoginSteps._
import com.linagora.openpaas.gatling.core.StaticAssetsSteps.{extractMainJsUrl, loadIndexHtml, loadIndexHtmlAndMainJs, loadMainJs}
import com.linagora.openpaas.gatling.core.{AvatarsSteps, PeopleSteps, StaticAssetsSteps}
import com.linagora.openpaas.gatling.core.TokenSteps.{generateJwtTokenWithAuth, retrieveAuthenticationToken}
import com.linagora.openpaas.gatling.core.WebSocketSteps._
import com.linagora.openpaas.gatling.provisionning.SessionKeys.UsernameSessionParam
import com.linagora.openpaas.gatling.utils.RandomHumanActionDelay._
import io.gatling.core.Predef._

import scala.concurrent.duration.DurationInt
import scala.util.Random

object InboxScenari {

  def generateOnceWithLogin() =
    exec(loadLoginTemplates)
      .exec(login)
      .exec(loadIndexHtmlAndMainJs(Configuration.inboxSpaPath))
      .exec(retrieveAuthenticationToken)
      .exec(getSocketId)
      .exec(registerSocketNamespaces)
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
    exec(retrieveAuthenticationToken)
      .exec(generateJwtTokenWithAuth)
      .pause(humanActionDelay() second)
      .exec(getMailboxes)
      .exec(getMessageList)
      .repeat(10)(exec(AvatarsSteps.search(UsernameSessionParam, withRandomDisplayName = true)))
      .pause(humanActionDelay() second)
      .group("do send email")(sendEmailSteps))


  def userLogin() = group("login")(exec(loadLoginTemplates)
    .exec(login)
    .exec(loadIndexHtmlAndMainJs(Configuration.inboxSpaPath))
    .exec(retrieveAuthenticationToken)
    .exec(getSocketId)
    .exec(registerSocketNamespaces)
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
    .exec(getMessageList))

  def userLogout() = group("logout")(exec(closeWsConnection)
    .pause(humanActionDelay() second)
    .exec(logout))

  def idle() = group("idle")(
    exec(getMailboxes)
      .exec(getMessageList)
      .repeat(3)(exec(AvatarsSteps.search(UsernameSessionParam, withRandomDisplayName = true)))
      .pause(60.seconds))

  private def sendEmailSteps = {
    exec(loadOpeningComposerTemplates)
      .pause(humanActionDelay() second)
      .exec(session => session.set("userNameFirstLetter", session(UsernameSessionParam).as[String].substring(0, 1)))
      .exec(session => session.set("userNameFirst3Letters", session(UsernameSessionParam).as[String].substring(0, 3)))
      .exec(PeopleSteps.search("userNameFirstLetter"))
      .exec(session => extractRandomAvatar(session, "avatarToLoadA"))
      .exec(session => extractRandomAvatar(session, "avatarToLoadB"))
      .exec(session => extractRandomAvatar(session, "avatarToLoadC"))
      .exec(AvatarsSteps.search("avatarToLoadA"))
      .exec(AvatarsSteps.search("avatarToLoadB"))
      .exec(AvatarsSteps.search("avatarToLoadC"))
      .pause(1 second)
      .exec(PeopleSteps.search("userNameFirst3Letters"))
      .exec(session => extractRandomAvatar(session, "avatarToLoadSecondQueryA"))
      .exec(session => extractRandomAvatar(session, "avatarToLoadSecondQueryB"))
      .exec(AvatarsSteps.search("avatarToLoadSecondQueryA"))
      .exec(AvatarsSteps.search("avatarToLoadSecondQueryB"))
      .pause(1 second)
      .exec(PeopleSteps.search(UsernameSessionParam))
      .exec(session => extractRandomAvatar(session, "avatarToLoadThirdQuery"))
      .exec(AvatarsSteps.search("avatarToLoadThirdQuery"))
      .pause(humanActionDelay() second)
      .exec(uploadAttachment)
      .pause(humanActionDelay() second)
      .exec(sendMessageWithAttachment)
  }

  private def extractRandomAvatar(session: Session, key: String) = {
    session.set(key, Random.shuffle(session(PeopleSteps.avatarsToLoadAfterSearch).as[Vector[String]]).head)
  }
}
