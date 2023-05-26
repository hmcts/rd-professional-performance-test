package scenarios.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import scala.concurrent.duration._
import scala.util.Random

object External_UpdateUserStatus {

  private val rng: Random = new Random()
  private def internalUser_firstName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_lastName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_Email(): String = rng.alphanumeric.take(15).mkString

  val config: Config = ConfigFactory.load()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val UpdateUserStatusString2 = "{ \"idamStatus\" : \"SUSPENDED\"}"
  val UpdateUserStatusString1 = "{ \"idamStatus\" : \"ACTIVE\"}"

  // val EditUsrStatusMin = config.getString("internal.editUsrStatusMin").toInt
  // val EditUsrStatusMax = config.getString("internal.editUsrStatusMax").toInt

  val UpdateInternalUserStatus =  repeat(1){

      exec(_.setAll(
          ("InternalUser_FirstName",internalUser_firstName()),
          ("InternalUser_LastName",internalUser_lastName()),
          ("InternalUser_Email",internalUser_Email())
        ))

      .feed(OrgIdData)

      .repeat(2){
        exec(http("RD30_External_UpdateUserStatus")
          .put("/refdata/external/v1/organisations/users/#{userId}?origin=EXUI")
          .header("Authorization", "Bearer #{accessToken}")
          .header("ServiceAuthorization", "Bearer #{s2sToken}")
          .body(StringBody(UpdateUserStatusString1))
          .header("Content-Type", "application/json")
          .check(status is 200))
      }

      .pause(Environment.thinkTime)

      .exec(http("RD31_External_UpdateUserStatus")
        .put("/refdata/external/v1/organisations/users/#{userId}?origin=EXUI")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "Bearer #{s2sToken}")
        .body(StringBody(UpdateUserStatusString2))
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(Environment.thinkTime)
  }
}