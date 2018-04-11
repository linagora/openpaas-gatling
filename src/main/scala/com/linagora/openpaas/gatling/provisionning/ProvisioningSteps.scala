package com.linagora.openpaas.gatling.provisionning

import com.linagora.openpaas.gatling.Configuration._
import com.linagora.openpaas.gatling.provisionning.SessionKeys._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object ProvisioningSteps {

  def provision() =
    exec(
      http("provisionning")
        .post(s"/api/domains/$DomainId/members")
        .basicAuth(PlatformAdminLogin, PlatformAdminPassword)
        .body(StringBody(
          s"""
{
  "password": "$${$PasswordSessionParam}",
  "accounts": [
    {
      "type": "email",
      "preferredEmailIndex": 0,
      "emails": [ "$${$UsernameSessionParam}"],
      "hosted": false
    }
  ],
  "domains": [ {"domain_id": "$DomainId"} ]
}
          """))
        .check(status.is(201)))

}
