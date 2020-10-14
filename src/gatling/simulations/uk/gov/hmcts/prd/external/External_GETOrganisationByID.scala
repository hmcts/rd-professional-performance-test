package uk.gov.hmcts.prd.external

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._
object External_GETOrganisationByID {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenExternal()

  val GetOrgByOrgIdMin = config.getString("external.getOrgByOrgIdMin").toInt

  val GetOrgByOrgIdMax = config.getString("external.getOrgByOrgIdMax").toInt

  val GETOrganisationByID = exec(http("RD12_External_GetOrganizationsByID")
    .get("/refdata/external/v1/organisations/${NewPendingOrg_Id2}")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "Bearer ${s2sToken}")
    .header("Content-Type", "application/json")
    .check(status is 200)
    .check(jsonPath("$..email").saveAs("email2")))
    .pause(GetOrgByOrgIdMin seconds, GetOrgByOrgIdMax seconds)
}
