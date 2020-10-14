package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._
import scala.util.Random

object External_UpdateUserStatus {

  private val rng: Random = new Random()
  private def internalUser_firstName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_lastName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_Email(): String = rng.alphanumeric.take(15).mkString

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val UpdateUserStatusString = "{ \"idamStatus\" : \"ACTIVE\"}"

  val EditUsrStatusMin = config.getString("internal.editUsrStatusMin").toInt

  val EditUsrStatusMax = config.getString("internal.editUsrStatusMax").toInt

  val internal_UpdateUserStatus =  repeat(1){

      exec(_.setAll(
          ("InternalUser_FirstName",internalUser_firstName()),
          ("InternalUser_LastName",internalUser_lastName()),
          ("InternalUser_Email",internalUser_Email())
        ))

      .feed(OrgIdData)

      .exec(http("R3_External_UpdateUserStatus")
          .put("/refdata/external/v1/organisations/${NewPendingOrg_Id2}/users/${userId2}?origin=EXUI")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .body(StringBody(UpdateUserStatusString))
        .header("Content-Type", "application/json")
        .check(status is 200))
      .pause(EditUsrStatusMin seconds, EditUsrStatusMax seconds)
  }
}
