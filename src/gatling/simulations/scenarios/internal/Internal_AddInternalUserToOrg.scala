package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import scala.util.Random
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_AddInternalUserToOrg {

  private val rng: Random = new Random()
  private def internalUser_firstName(): String = rng.alphanumeric.take(20).mkString
  private def internalUser_lastName(): String = rng.alphanumeric.take(20).mkString

  val config: Config = ConfigFactory.load()
  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val AddInternalUserToOrg = 
  
    exec(_.setAll(
        ("InternalUser_FirstName",internalUser_firstName()),
        ("InternalUser_LastName",internalUser_lastName())
      ))

    .feed(OrgIdData)

    .exec(http("RD06_Internal_AddInternalUserToOrganisation")
      .post("/refdata/internal/v1/organisations/#{NewPendingOrg_Id}/users/")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "Bearer #{s2sToken}")
      .body(ElFileBody("bodies/internal/AddInternalUser.json"))
      .header("Content-Type", "application/json")
      .check(status is 201)
      .check(jsonPath("$.userIdentifier").saveAs("userId")))

    .pause(Environment.thinkTime)
}