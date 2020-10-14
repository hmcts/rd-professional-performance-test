package uk.gov.hmcts.prd.external

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.prd.util._

import scala.concurrent.duration._

object External_GETOrganisationByID {

  val config: Config = ConfigFactory.load()

  val s2sToken = PRDTokenGenerator.generateS2SToken()

  val IdAMToken = PRDTokenGenerator.generateSIDAMUserTokenInternal()

  val OrgIdData = csv("prdIntOrgIDs.csv").circular

  val GetOrgByOrgIdMin = config.getString("internal.getOrgByOrgIdMin").toInt

  val GetOrgByOrgIdMax = config.getString("internal.getOrgByOrgIdMax").toInt

  val GETOrganisationByID = feed(OrgIdData)

    .exec(http("RD04_Internal_GetOrganizationsByID")
      .get("/refdata/internal/v1/organisations?id=${NewPendingOrg_Id2}")
      .header("Authorization", "Bearer ${accessToken}")
      .header("ServiceAuthorization", "Bearer ${s2sToken}")
      .header("Content-Type", "application/json")
      .check(status is 200)
      .check(jsonPath("$..email").saveAs("email2")))
      .pause(GetOrgByOrgIdMin seconds, GetOrgByOrgIdMax seconds)
}
