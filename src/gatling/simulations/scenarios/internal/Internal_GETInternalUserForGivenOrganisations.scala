package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETInternalUserForGivenOrganisations {

  val config: Config = ConfigFactory.load()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GETInternalUserForGivenOrganisations = 
  
    feed(OrgIdData)

    .repeat(1) {
      exec(http("RD07_Internal_GetInternalUserForGivenOrganisation")
        .get("/refdata/internal/v1/organisations/#{NewPendingOrg_Id}/users?showdeleted=false&rolesRequired=false")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "Bearer #{s2sToken}")
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(Environment.thinkTime)
    }
}