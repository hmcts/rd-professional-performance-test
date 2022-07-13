package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._

object External_GETStatusInternalUserForActiveOrganisationByEmail {

  val config: Config = ConfigFactory.load()

  // val GetIntUsrByOrgEmailMin = config.getString("external.getIntUsrByOrgEmailMin").toInt
  // val GetIntUsrByOrgEmailMax = config.getString("external.getIntUsrByOrgEmailMax").toInt

  val GETStatusInternalUserForActiveOrganisationByEmail = 

    repeat(1) {
    
      exec(http("RD28_External_GetStatusInternalUserForActiveOrganisationByEmailAddress")
        .get("/refdata/external/v1/organisations/users/accountId?email=${adminEmail}")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(Environment.thinkTime)
    }
}