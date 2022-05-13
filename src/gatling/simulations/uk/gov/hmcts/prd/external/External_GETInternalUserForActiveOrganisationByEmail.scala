package uk.gov.hmcts.prd.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object External_GETInternalUserForActiveOrganisationByEmail {

  val config: Config = ConfigFactory.load()

  // val GetIntUsrByOrgEmailMin = config.getString("external.getIntUsrByOrgEmailMin").toInt
  // val GetIntUsrByOrgEmailMax = config.getString("external.getIntUsrByOrgEmailMax").toInt

  val GETInternalUserForActiveOrganisationByEmail = 

    repeat(1) {
  
      exec(http("RD23_External_GetInternalUserForActiveOrganisationByEmailAddress")
        .get("/refdata/external/v1/organisations/users?email=${Email}")
        .header("Authorization", "Bearer ${accessToken}")
        .header("ServiceAuthorization", "Bearer ${s2sToken}")
        .header("Content-Type", "application/json")
        .check(status is 200))

      .pause(Environment.thinkTime)
    }
}