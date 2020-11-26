package com.linagora.openpaas.gatling.unifiedinbox

import com.linagora.openpaas.gatling.Configuration

object LoginTemplateRequestsList {
  val redirectToUnifiedInboxPageAfterLogin = Array(
    "/socket.io/socket.io.js",
    s"${Configuration.LemonLDAPPortalUrl}/static/bwr/jquery/dist/jquery.min.js",
    s"${Configuration.LemonLDAPPortalUrl}/static/common/js/oidcchecksession.min.js",
    s"${Configuration.LemonLDAPPortalUrl}/static/common/js/crypto-js.min.js",
    s"${Configuration.LemonLDAPPortalUrl}/static/common/js/hmac-sha256.min.js",
    s"${Configuration.LemonLDAPPortalUrl}/static/common/js/enc-base64.min.js",
    "/inbox-oidc/inbox-assets/apple-touch-icon-1024x1024.png",
    "/inbox-oidc/inbox-assets/favicon-16x16.png",
    "/images/mdi/mdi.svg",
    "/images/logo-tiny.png",
    "/components/roboto-fontface/fonts/Roboto-Bold.woff2",
    "/images/white-logo.svg"
  )

  val openingComposerTemplates = Array(
    "/components/summernote/dist/font/summernote.woff?3297c53f47926b9d4fc132301b6ab80e",
    "/images/upload.svg",
    "/images/exclamation.svg",
    "/images/file-icons/default.png"
  )
}
