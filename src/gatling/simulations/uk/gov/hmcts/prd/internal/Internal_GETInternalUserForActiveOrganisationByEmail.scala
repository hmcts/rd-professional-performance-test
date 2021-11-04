package uk.gov.hmcts.prd.internal

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object Internal_GETInternalUserForActiveOrganisationByEmail {

  val config: Config = ConfigFactory.load()
  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GETInternalUserForActiveOrganisationByEmail = 
  
    exec(http("RD08_Internal_GetInternalUserForActiveOrganisationByEmailAddress")
      .get("/refdata/internal/v1/organisations/${NewPendingOrg_Id}/users?email=${Email}")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 200))

    .pause(Environment.thinkTime)
}