package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.Configuration

object LoginTemplateRequestsList {
  private val inboxPath = removeTrailingSlash(Configuration.InboxSpaPath)

  val redirectToUnifiedInboxPageAfterLogin = Array(
    s"/${inboxPath}/assets/socket.io/socket.io.js",
    s"/${inboxPath}/assets/env/openpaas.js",
    s"/${inboxPath}/assets/c065bd9fa03a7a797d95ba7f198a9dcf.woff2",
    s"/${inboxPath}/assets/favicon/favicon.ico",
    s"/${inboxPath}/assets/fa6ffda3f32ae17872e2f5dbc1332b96.svg",
    s"/${inboxPath}/assets/images/white-logo.svg",
    s"/${inboxPath}/assets/93ba1198dd07efc2e2e767e30d5b16eb.woff2",
    s"/${inboxPath}/assets/b92cc2170a4d5438fd3d19f349ce3785.woff2",
    s"/${inboxPath}/assets/images/logo-tiny.png",
    s"/${inboxPath}/assets/3b52bc86749058f144deb815c481ca5b.woff2",
    s"/${inboxPath}/assets/images/throbber-amber.svg",
    s"/${inboxPath}/assets/3a0ee5b0beec8d0ead1336016cbe19ad.woff2",
    s"/${inboxPath}/assets/f21b7e045fd077321cdaf92cab817cd3.woff2"
  )

  val openingComposerTemplates = Array(
    s"/${inboxPath}/assets/images/5144110a3e5d36b091e0ffcad81a7d46.png",
    s"/${inboxPath}/assets/113e7623163d4cb7f965cd8f8d3859eb.woff2",
    s"/${inboxPath}/assets/images/exclamation.svg",
    s"/${inboxPath}/assets/images/upload.svg",
    s"/${inboxPath}/assets/images/file-icons/default.png",
    s"/${inboxPath}/assets/f836a4769313f4c75acf7cf1d2fd31c2.woff"
  )

  private def removeTrailingSlash(url: String): String = url.replaceAll("/$", "")

}
