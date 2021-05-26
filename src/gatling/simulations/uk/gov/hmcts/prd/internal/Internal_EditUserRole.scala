package uk.gov.hmcts.prd.internal

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import scala.concurrent.duration._

object Internal_EditUserRole {

  val config: Config = ConfigFactory.load()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val editInternalUserRoleString = "{\n \"rolesAdd\": [ { \"name\": \"pui-case-manager\" }, { \"name\": \"caseworker\" } ], \"rolesDelete\": [ { \"name\": \"pui-case-manager\" }, { \"name\": \"caseworker\" } ]}\n"

  val EditUsrRoleMin = config.getString("internal.editUsrRoleMin").toInt

  val EditUsrRoleMax = config.getString("internal.editUsrRoleMax").toInt

  val EditInternalUserRole =  repeat(1){

      feed(OrgIdData)

      .exec(http("RD13_Internal_EditUserRole")
          .put("/refdata/internal/v1/organisations/${NewPendingOrg_Id}/users/${userId}?origin=EXUI")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .body(StringBody(editInternalUserRoleString))
        .header("Content-Type", "application/json")
        .check(status is 200))
      .pause(EditUsrRoleMin seconds, EditUsrRoleMax seconds)
  }
}
