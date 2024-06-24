package scenarios.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object Internal_GETInternalUserForActiveOrganisationByEmail {

  val config: Config = ConfigFactory.load()
  
  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GETInternalUserForActiveOrganisationByEmail = 
  
    exec(http("RD09_Internal_GetInternalUserForActiveOrganisationByEmailAddress")
      .get("/refdata/internal/v1/organisations/#{NewPendingOrg_Id}/users?email=#{Email}")
      .header("Authorization", "Bearer #{accessToken}")
      .header("ServiceAuthorization", "Bearer #{s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 200))

    .pause(Environment.thinkTime)

  val GETInternalUserDetailsForActiveOrganisationByEmail = 

    repeat(10) {

      exec(http("RD10_Internal_GetInternalSuperUserOrgDetails")
        .get("/refdata/internal/v1/organisations/orgDetails/#{superUserId}")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "Bearer #{s2sToken}")
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(Environment.thinkTime)
    
      .exec(http("RD11_Internal_GetInternalUserOrgDetails")
        .get("/refdata/internal/v1/organisations/orgDetails/#{userId}")
        .header("Authorization", "Bearer #{accessToken}")
        .header("ServiceAuthorization", "Bearer #{s2sToken}")
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(Environment.thinkTime)
    }
}