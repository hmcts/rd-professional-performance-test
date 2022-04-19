package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._
import scala.util.Random

object External_EditUserRole {

  private val rng: Random = new Random()
  private def internalUser_firstName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_lastName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_Email(): String = rng.alphanumeric.take(15).mkString

  val config: Config = ConfigFactory.load()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val editInternalUserRoleString = "{\n \"rolesAdd\": [ { \"name\": \"pui-case-manager\" }, { \"name\": \"caseworker\" } ], \"rolesDelete\": [ { \"name\": \"pui-case-manager\" }, { \"name\": \"caseworker\" } ]}\n"

  // val EditUsrRoleMin = config.getString("internal.editUsrRoleMin").toInt
  // val EditUsrRoleMax = config.getString("internal.editUsrRoleMax").toInt

  val EditInternalUserRole =  repeat(1){

      exec(_.setAll(
          ("InternalUser_FirstName",internalUser_firstName()),
          ("InternalUser_LastName",internalUser_lastName()),
          ("InternalUser_Email",internalUser_Email())
        ))

      .feed(OrgIdData)

      .exec(http("RD24_External_EditUserRole")
          .put("/refdata/external/v1/organisations/users/${userId}?origin=EXUI")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .body(StringBody(editInternalUserRoleString))
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(Environment.thinkTime)
  }
}